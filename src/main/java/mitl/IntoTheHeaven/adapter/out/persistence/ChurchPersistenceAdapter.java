package mitl.IntoTheHeaven.adapter.out.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchMemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.ChurchJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.ChurchMemberJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.ChurchPersistenceMapper;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.ChurchMemberPersistenceMapper;
import mitl.IntoTheHeaven.application.dto.MemberWithGroups;
import mitl.IntoTheHeaven.application.port.out.ChurchPort;
import mitl.IntoTheHeaven.domain.enums.ChurchRole;
import mitl.IntoTheHeaven.domain.model.Church;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMember;
import mitl.IntoTheHeaven.domain.model.MemberId;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static mitl.IntoTheHeaven.adapter.out.persistence.entity.QChurchMemberJpaEntity.churchMemberJpaEntity;
import static mitl.IntoTheHeaven.adapter.out.persistence.entity.QMemberJpaEntity.memberJpaEntity;
import static mitl.IntoTheHeaven.adapter.out.persistence.entity.QGroupMemberJpaEntity.groupMemberJpaEntity;
import static mitl.IntoTheHeaven.adapter.out.persistence.entity.QGroupJpaEntity.groupJpaEntity;

@Component
@RequiredArgsConstructor
public class ChurchPersistenceAdapter implements ChurchPort {

        private final ChurchJpaRepository churchJpaRepository;
        private final ChurchMemberJpaRepository churchMemberJpaRepository;
        private final ChurchPersistenceMapper churchPersistenceMapper;
        private final ChurchMemberPersistenceMapper churchMemberPersistenceMapper;
        private final JPAQueryFactory queryFactory;

        @Override
        public List<Church> findChurchesByMemberId(UUID memberId) {
                return churchMemberJpaRepository.findAllByMemberId(memberId)
                                .stream()
                                .map(churchMember -> churchPersistenceMapper.toDomain(churchMember.getChurch()))
                                .toList();
        }

        @Override
        public Church findById(UUID churchId) {
                return churchPersistenceMapper.toDomain(churchJpaRepository.findById(churchId)
                                .orElse(null));
        }

        @Override
        public List<MemberId> findMemberIdsByChurchId(UUID churchId) {
                return churchMemberJpaRepository.findAllByChurchId(churchId)
                                .stream()
                                .map(churchMember -> MemberId.from(churchMember.getMember().getId()))
                                .toList();
        }

        @Override
        public ChurchMember findChurchMemberByMemberIdAndChurchId(MemberId memberId, ChurchId churchId) {
                ChurchMemberJpaEntity churchMemberJpaEntity = churchMemberJpaRepository
                                .findByMemberIdAndChurchId(memberId.getValue(), churchId.getValue());
                if (churchMemberJpaEntity == null) {
                        return null;
                }
                return churchMemberPersistenceMapper.toDomain(
                                churchMemberJpaEntity);
        }

        @Override
        public List<ChurchMember> findChurchMembersByMemberIdAndRole(MemberId memberId, ChurchRole role) {
                return churchMemberJpaRepository.findAllByMemberIdAndRole(memberId.getValue(), role)
                                .stream()
                                .map(churchMember -> churchMemberPersistenceMapper.toDomain(churchMember))
                                .toList();
        }

        @Override
        public List<MemberWithGroups> findMembersByChurchIdAndSearch(UUID churchId, String searchText) {
                // QueryDSL로 해당 교회의 멤버와 해당 교회의 그룹만 JOIN
                List<ChurchMemberJpaEntity> churchMemberEntities = queryFactory
                                .selectFrom(churchMemberJpaEntity)
                                .join(churchMemberJpaEntity.member, memberJpaEntity).fetchJoin()
                                .leftJoin(memberJpaEntity.groupMembers, groupMemberJpaEntity).fetchJoin()
                                .leftJoin(groupMemberJpaEntity.group, groupJpaEntity).fetchJoin()
                                .where(
                                                churchMemberJpaEntity.church.id.eq(churchId),
                                                // 그룹이 없거나, 그룹이 해당 교회에 속한 경우만
                                                groupMemberJpaEntity.isNull()
                                                                .or(groupJpaEntity.church.id.eq(churchId)),
                                                // 검색 조건
                                                memberJpaEntity.name.contains(searchText)
                                                                .or(memberJpaEntity.phone.contains(searchText)))
                                .orderBy(memberJpaEntity.birthday.asc())
                                .distinct()
                                .fetch();

                // distinct() 덕분에 각 멤버는 한 번씩만 조회되고, 이미 해당 교회 그룹만 JOIN되어 있음
                return churchMemberEntities.stream()
                                .map(churchMemberEntity -> {
                                        MemberJpaEntity memberEntity = churchMemberEntity.getMember();

                                        List<MemberWithGroups.GroupInfo> groups = memberEntity.getGroupMembers()
                                                        .stream()
                                                        .filter(gm -> gm.getGroup() != null
                                                                        && gm.getGroup().getChurch().getId()
                                                                                        .equals(churchId))
                                                        .map(gm -> MemberWithGroups.GroupInfo.builder()
                                                                        .groupId(gm.getGroup().getId())
                                                                        .groupName(gm.getGroup().getName())
                                                                        .role(gm.getRole())
                                                                        .build())
                                                        .collect(Collectors.toList());

                                        return MemberWithGroups.builder()
                                                        .id(MemberId.from(memberEntity.getId()))
                                                        .name(memberEntity.getName())
                                                        .email(memberEntity.getEmail())
                                                        .sex(memberEntity.getSex())
                                                        .birthday(memberEntity.getBirthday())
                                                        .phone(memberEntity.getPhone())
                                                        .address(memberEntity.getAddress())
                                                        .description(memberEntity.getDescription())
                                                        .groups(groups)
                                                        .build();
                                })
                                .collect(Collectors.toList());
        }
}
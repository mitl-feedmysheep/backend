package mitl.IntoTheHeaven.adapter.out.persistence;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchMemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchMemberRequestJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.ChurchJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.ChurchMemberJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.ChurchMemberRequestJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.ChurchPersistenceMapper;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.ChurchMemberPersistenceMapper;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.ChurchMemberRequestPersistenceMapper;
import mitl.IntoTheHeaven.application.dto.MemberWithGroups;
import mitl.IntoTheHeaven.application.port.out.ChurchPort;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.enums.RequestStatus;
import mitl.IntoTheHeaven.domain.model.Church;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMember;
import mitl.IntoTheHeaven.domain.model.ChurchMemberRequest;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.MemberPersistenceMapper;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
        private final ChurchMemberRequestJpaRepository churchMemberRequestJpaRepository;
        private final ChurchPersistenceMapper churchPersistenceMapper;
        private final ChurchMemberPersistenceMapper churchMemberPersistenceMapper;
        private final ChurchMemberRequestPersistenceMapper churchMemberRequestPersistenceMapper;
        private final MemberPersistenceMapper memberPersistenceMapper;
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
        public List<Member> findBirthdayMembersByChurchIdAndMonth(UUID churchId, int month) {
                List<MemberJpaEntity> members = queryFactory
                                .select(memberJpaEntity)
                                .from(churchMemberJpaEntity)
                                .join(churchMemberJpaEntity.member, memberJpaEntity)
                                .where(
                                                churchMemberJpaEntity.church.id.eq(churchId),
                                                memberJpaEntity.birthday.isNotNull(),
                                                Expressions.numberTemplate(Integer.class,
                                                                "MONTH({0})", memberJpaEntity.birthday).eq(month))
                                .orderBy(Expressions.numberTemplate(Integer.class,
                                                "DAY({0})", memberJpaEntity.birthday).asc())
                                .fetch();

                return members.stream()
                                .map(memberPersistenceMapper::toDomain)
                                .toList();
        }

        @Override
        public List<Church> findAllChurches() {
                return churchJpaRepository.findAll()
                                .stream()
                                .map(churchPersistenceMapper::toDomain)
                                .toList();
        }

        @Override
        public Optional<ChurchMemberRequest> findPendingJoinRequest(UUID memberId, UUID churchId) {
                return churchMemberRequestJpaRepository
                                .findByMemberIdAndChurchIdAndStatus(memberId, churchId, RequestStatus.PENDING)
                                .map(churchMemberRequestPersistenceMapper::toDomain);
        }

        @Override
        public List<ChurchMemberRequest> findJoinRequestsByMemberId(UUID memberId) {
                return churchMemberRequestJpaRepository.findAllByMemberId(memberId)
                                .stream()
                                .map(churchMemberRequestPersistenceMapper::toDomain)
                                .toList();
        }

        @Override
        public ChurchMemberRequest saveJoinRequest(ChurchMemberRequest request) {
                ChurchMemberRequestJpaEntity entity = ChurchMemberRequestJpaEntity.builder()
                                .id(request.getId().getValue())
                                .member(MemberJpaEntity.builder().id(request.getMemberId().getValue()).build())
                                .church(ChurchJpaEntity.builder().id(request.getChurchId().getValue()).build())
                                .status(request.getStatus())
                                .build();
                ChurchMemberRequestJpaEntity saved = churchMemberRequestJpaRepository.save(entity);

                return ChurchMemberRequest.builder()
                                .id(request.getId())
                                .memberId(request.getMemberId())
                                .churchId(request.getChurchId())
                                .status(saved.getStatus())
                                .churchName(request.getChurchName())
                                .createdAt(saved.getCreatedAt())
                                .updatedAt(saved.getUpdatedAt())
                                .build();
        }

        @Override
        public List<GroupMemberRole> findGroupMemberRolesByMemberIdAndChurchId(UUID memberId, UUID churchId) {
                int currentYear = LocalDate.now().getYear();

                return queryFactory
                                .select(groupMemberJpaEntity.role)
                                .from(groupMemberJpaEntity)
                                .join(groupMemberJpaEntity.group, groupJpaEntity)
                                .where(
                                                groupMemberJpaEntity.member.id.eq(memberId),
                                                groupJpaEntity.church.id.eq(churchId),
                                                groupJpaEntity.endDate.isNotNull(),
                                                groupJpaEntity.endDate.year().eq(currentYear))
                                .fetch();
        }

        @Override
        public List<ChurchMember> findChurchMembersByMemberId(MemberId memberId) {
                return churchMemberJpaRepository.findAllByMemberId(memberId.getValue())
                                .stream()
                                .map(churchMemberPersistenceMapper::toDomain)
                                .collect(Collectors.toList());
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
        public List<MemberWithGroups> findMembersByChurchIdAndSearch(UUID churchId, String searchText) {
                // Get current year for filtering groups
                int currentYear = LocalDate.now().getYear();

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

                                        // Filter groups by current year (endDate year equals current year)
                                        List<MemberWithGroups.GroupInfo> groups = memberEntity.getGroupMembers()
                                                        .stream()
                                                        .filter(gm -> gm.getGroup() != null
                                                                        && gm.getGroup().getChurch().getId()
                                                                                        .equals(churchId)
                                                                        && gm.getGroup().getEndDate() != null
                                                                        && gm.getGroup().getEndDate().getYear() == currentYear)
                                                        .map(gm -> MemberWithGroups.GroupInfo.builder()
                                                                        .groupId(gm.getGroup().getId())
                                                                        .groupName(gm.getGroup().getName())
                                                                        .role(gm.getRole())
                                                                        .build())
                                                        .collect(Collectors.toList());

                                        return MemberWithGroups.builder()
                                                        .id(MemberId.from(memberEntity.getId()))
                                                        .churchMemberId(churchMemberEntity.getId())
                                                        .name(memberEntity.getName())
                                                        .email(memberEntity.getEmail())
                                                        .sex(memberEntity.getSex())
                                                        .birthday(memberEntity.getBirthday())
                                                        .phone(memberEntity.getPhone())
                                                        .address(memberEntity.getAddress())
                                                        .description(memberEntity.getDescription())
                                                        .occupation(memberEntity.getOccupation())
                                                        .baptismStatus(memberEntity.getBaptismStatus())
                                                        .mbti(memberEntity.getMbti())
                                                        .groups(groups)
                                                        .build();
                                })
                                .collect(Collectors.toList());
        }
}
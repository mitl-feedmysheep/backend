package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupMemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.MemberPersistenceMapper;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.MemberJpaRepository;
import mitl.IntoTheHeaven.application.port.out.MemberPort;
import mitl.IntoTheHeaven.domain.model.GroupMember;
import mitl.IntoTheHeaven.domain.model.Member;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements MemberPort {

  private final MemberJpaRepository memberJpaRepository;
  private final MemberPersistenceMapper memberPersistenceMapper;

  @Override
  public List<Member> findMembersByGroupId(UUID groupId) {
    return memberJpaRepository.findAllByGroupMembers_Group_Id(groupId).stream()
            .map(memberPersistenceMapper::toDomain)
            .collect(Collectors.toList());
  }

  @Override
  public Optional<Member> findById(UUID memberId) {
    return memberJpaRepository.findById(memberId)
            .map(memberPersistenceMapper::toDomain);
  }

  @Override
  public Member save(Member member) {
    MemberJpaEntity entity = memberPersistenceMapper.toEntity(member);
    MemberJpaEntity savedEntity = memberJpaRepository.save(entity);
    return memberPersistenceMapper.toDomain(savedEntity);
  }

  @Override
  public List<GroupMember> findGroupMembersByGroupId(UUID groupId) {
    return memberJpaRepository.findAllWithGroupMembersByGroupMembers_Group_Id(groupId).stream()
            .flatMap(memberEntity -> memberEntity.getGroupMembers().stream())
            .filter(groupMemberEntity -> groupMemberEntity.getGroup().getId().equals(groupId))
            .map(memberPersistenceMapper::toGroupMemberDomain)
            .collect(Collectors.toList());
  }

  @Override
  public Optional<Member> findByEmail(String email) {
    return memberJpaRepository.findByEmail(email)
            .map(memberPersistenceMapper::toDomain);
  }
} 
package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.MemberPersistenceMapper;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.MemberJpaRepository;
import mitl.IntoTheHeaven.application.port.out.MemberPort;
import mitl.IntoTheHeaven.domain.model.Member;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements MemberPort {

  private final MemberJpaRepository memberJpaRepository;
  private final MemberPersistenceMapper memberPersistenceMapper;

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
} 
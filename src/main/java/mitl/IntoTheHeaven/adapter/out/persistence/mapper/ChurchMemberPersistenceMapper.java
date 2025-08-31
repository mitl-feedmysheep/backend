package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchMemberJpaEntity;
import mitl.IntoTheHeaven.domain.model.ChurchMember;
import mitl.IntoTheHeaven.domain.model.ChurchMemberId;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.springframework.stereotype.Component;

@Component
public class ChurchMemberPersistenceMapper {
  public ChurchMember toDomain(ChurchMemberJpaEntity entity) {
    return ChurchMember.builder()
        .id(ChurchMemberId.from(entity.getId()))
        .churchId(ChurchId.from(entity.getChurch().getId()))
        .memberId(MemberId.from(entity.getMember().getId()))
        .role(entity.getRole())
        .build();
  }
}

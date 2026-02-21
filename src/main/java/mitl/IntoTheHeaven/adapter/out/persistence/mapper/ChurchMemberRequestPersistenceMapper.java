package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchMemberRequestJpaEntity;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMemberRequest;
import mitl.IntoTheHeaven.domain.model.ChurchMemberRequestId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.springframework.stereotype.Component;

@Component
public class ChurchMemberRequestPersistenceMapper {

    public ChurchMemberRequest toDomain(ChurchMemberRequestJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ChurchMemberRequest.builder()
                .id(ChurchMemberRequestId.from(entity.getId()))
                .memberId(MemberId.from(entity.getMember().getId()))
                .churchId(ChurchId.from(entity.getChurch().getId()))
                .status(entity.getStatus())
                .churchName(entity.getChurch().getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }
}

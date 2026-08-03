package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchJpaEntity;
import mitl.IntoTheHeaven.domain.model.Church;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import org.springframework.stereotype.Component;

@Component
public class ChurchPersistenceMapper {

    public Church toDomain(ChurchJpaEntity entity) {
        return Church.builder()
                .id(ChurchId.from(entity.getId()))
                .name(entity.getName())
                .location(entity.getLocation())
                .number(entity.getNumber())
                .homepageUrl(entity.getHomepageUrl())
                .description(entity.getDescription())
                .isHidden(entity.getIsHidden())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public ChurchJpaEntity toEntity(Church domain) {
        return ChurchJpaEntity.builder()
                .id(domain.getId().getValue())
                .name(domain.getName())
                .location(domain.getLocation())
                .number(domain.getNumber())
                .homepageUrl(domain.getHomepageUrl())
                .description(domain.getDescription())
                .isHidden(domain.isHidden())
                .build();
    }
}
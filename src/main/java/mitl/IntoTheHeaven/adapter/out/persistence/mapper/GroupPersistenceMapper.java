package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchJpaEntity;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.Group;
import mitl.IntoTheHeaven.domain.model.GroupId;
import org.springframework.stereotype.Component;

@Component
public class GroupPersistenceMapper {

    public Group toDomain(GroupJpaEntity entity) {
        return Group.builder()
                .id(GroupId.from(entity.getId()))
                .name(entity.getName())
                .description(entity.getDescription())
                .churchId(ChurchId.from(entity.getChurch().getId()))
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .build();
    }

    public GroupJpaEntity toEntity(Group domain) {
        return GroupJpaEntity.builder()
                .id(domain.getId().getValue())
                .name(domain.getName())
                .description(domain.getDescription())
                .church(ChurchJpaEntity.builder().id(domain.getChurchId().getValue()).build())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .build();
    }
} 
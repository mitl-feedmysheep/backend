package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupJpaEntity;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.Group;
import mitl.IntoTheHeaven.domain.model.GroupId;
import org.springframework.stereotype.Component;

@Component
public class GroupPersistenceMapper {

    public Group toDomain(GroupJpaEntity entity) {
        return Group.builder()
                .id(new GroupId(entity.getId()))
                .name(entity.getName())
                .description(entity.getDescription())
                .churchId(new ChurchId(entity.getChurchId()))
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .build();
    }

    public GroupJpaEntity toJpaEntity(Group domain) {
        return GroupJpaEntity.builder()
                .id(domain.getId().getValue())
                .name(domain.getName())
                .description(domain.getDescription())
                .churchId(domain.getChurchId().getValue())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .build();
    }
} 
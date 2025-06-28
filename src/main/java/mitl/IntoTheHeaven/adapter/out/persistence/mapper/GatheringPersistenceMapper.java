package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.GatheringJpaEntity;
import mitl.IntoTheHeaven.domain.model.Gathering;
import mitl.IntoTheHeaven.domain.model.GatheringId;
import mitl.IntoTheHeaven.domain.model.GroupId;
import org.springframework.stereotype.Component;

@Component
public class GatheringPersistenceMapper {

    public Gathering toDomain(GatheringJpaEntity entity) {
        return Gathering.builder()
                .id(new GatheringId(entity.getId()))
                .groupId(new GroupId(entity.getGroupId()))
                .name(entity.getName())
                .date(entity.getDate())
                .place(entity.getPlace())
                .build();
    }
} 
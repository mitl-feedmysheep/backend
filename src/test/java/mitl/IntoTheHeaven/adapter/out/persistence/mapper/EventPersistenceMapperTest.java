package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.EventJpaEntity;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.model.Event;
import mitl.IntoTheHeaven.domain.model.EventId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EventPersistenceMapperTest {

    private final EventPersistenceMapper mapper = new EventPersistenceMapper();

    @Test
    @DisplayName("toDomain: null 입력 시 null을 반환한다")
    void toDomain_nullInput() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("toDomain: JPA Entity -> Domain 변환 시 String 시간이 LocalTime으로 올바르게 변환된다")
    void toDomain() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        EventJpaEntity entity = EventJpaEntity.builder()
                .id(id)
                .entityId("gathering-123")
                .entityType(EntityType.GATHERING)
                .title("주일 예배")
                .description("주일 예배 일정")
                .startDate(LocalDate.of(2025, 6, 1))
                .endDate(LocalDate.of(2025, 6, 1))
                .startTime("09:30:00")
                .endTime("11:00:00")
                .location("본당")
                .createdAt(now)
                .updatedAt(now.plusHours(1))
                .deletedAt(now.plusDays(1))
                .build();

        Event domain = mapper.toDomain(entity);

        assertThat(domain.getId().getValue()).isEqualTo(id);
        assertThat(domain.getEntityId()).isEqualTo("gathering-123");
        assertThat(domain.getEntityType()).isEqualTo(EntityType.GATHERING);
        assertThat(domain.getTitle()).isEqualTo("주일 예배");
        assertThat(domain.getDescription()).isEqualTo("주일 예배 일정");
        assertThat(domain.getStartDate()).isEqualTo(LocalDate.of(2025, 6, 1));
        assertThat(domain.getEndDate()).isEqualTo(LocalDate.of(2025, 6, 1));
        assertThat(domain.getStartTime()).isEqualTo(LocalTime.of(9, 30, 0));
        assertThat(domain.getEndTime()).isEqualTo(LocalTime.of(11, 0, 0));
        assertThat(domain.getLocation()).isEqualTo("본당");
        assertThat(domain.getCreatedAt()).isEqualTo(now);
        assertThat(domain.getUpdatedAt()).isEqualTo(now.plusHours(1));
        assertThat(domain.getDeletedAt()).isEqualTo(now.plusDays(1));
    }

    @Test
    @DisplayName("toDomain: 시간 문자열이 null이거나 빈 문자열이면 LocalTime은 null이 된다")
    void toDomain_nullAndBlankTime() {
        EventJpaEntity entityWithNull = EventJpaEntity.builder()
                .id(UUID.randomUUID())
                .entityType(EntityType.GROUP)
                .startDate(LocalDate.now())
                .startTime(null)
                .endTime(null)
                .build();

        Event domainNull = mapper.toDomain(entityWithNull);
        assertThat(domainNull.getStartTime()).isNull();
        assertThat(domainNull.getEndTime()).isNull();

        EventJpaEntity entityWithBlank = EventJpaEntity.builder()
                .id(UUID.randomUUID())
                .entityType(EntityType.GROUP)
                .startDate(LocalDate.now())
                .startTime("   ")
                .endTime("")
                .build();

        Event domainBlank = mapper.toDomain(entityWithBlank);
        assertThat(domainBlank.getStartTime()).isNull();
        assertThat(domainBlank.getEndTime()).isNull();
    }

    @Test
    @DisplayName("toEntity: null 입력 시 null을 반환한다")
    void toEntity_nullInput() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("toEntity: Domain -> JPA Entity 변환 시 LocalTime이 HH:mm:ss 문자열로 올바르게 변환된다")
    void toEntity() {
        UUID id = UUID.randomUUID();

        Event domain = Event.builder()
                .id(EventId.from(id))
                .entityId("group-456")
                .entityType(EntityType.GROUP)
                .title("소그룹 모임")
                .description("금요 모임")
                .startDate(LocalDate.of(2025, 7, 4))
                .endDate(LocalDate.of(2025, 7, 4))
                .startTime(LocalTime.of(19, 0, 0))
                .endTime(LocalTime.of(21, 30, 0))
                .location("교육관 3층")
                .build();

        EventJpaEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getEntityId()).isEqualTo("group-456");
        assertThat(entity.getEntityType()).isEqualTo(EntityType.GROUP);
        assertThat(entity.getTitle()).isEqualTo("소그룹 모임");
        assertThat(entity.getDescription()).isEqualTo("금요 모임");
        assertThat(entity.getStartDate()).isEqualTo(LocalDate.of(2025, 7, 4));
        assertThat(entity.getEndDate()).isEqualTo(LocalDate.of(2025, 7, 4));
        assertThat(entity.getStartTime()).isEqualTo("19:00:00");
        assertThat(entity.getEndTime()).isEqualTo("21:30:00");
        assertThat(entity.getLocation()).isEqualTo("교육관 3층");
    }

    @Test
    @DisplayName("toEntity: LocalTime이 null이면 시간 문자열도 null이 된다")
    void toEntity_nullTime() {
        Event domain = Event.builder()
                .id(EventId.from(UUID.randomUUID()))
                .entityType(EntityType.CHURCH)
                .startDate(LocalDate.now())
                .startTime(null)
                .endTime(null)
                .build();

        EventJpaEntity entity = mapper.toEntity(domain);

        assertThat(entity.getStartTime()).isNull();
        assertThat(entity.getEndTime()).isNull();
    }

    @Test
    @DisplayName("시간 변환 라운드트립: Entity -> Domain -> Entity 시 시간 값이 보존된다")
    void timeConversion_roundtrip() {
        EventJpaEntity original = EventJpaEntity.builder()
                .id(UUID.randomUUID())
                .entityId("test-entity")
                .entityType(EntityType.GATHERING)
                .title("테스트")
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .startTime("14:30:00")
                .endTime("16:45:00")
                .build();

        Event domain = mapper.toDomain(original);
        EventJpaEntity result = mapper.toEntity(domain);

        assertThat(result.getStartTime()).isEqualTo("14:30:00");
        assertThat(result.getEndTime()).isEqualTo("16:45:00");
    }
}

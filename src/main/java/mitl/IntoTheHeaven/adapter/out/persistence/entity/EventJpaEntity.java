package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.global.common.BaseEntity;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Table(name = "`event`")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SQLRestriction("deleted_at is null")
public class EventJpaEntity extends BaseEntity {

    @Column(name = "entity_id", nullable = false, columnDefinition = "CHAR(36)")
    private String entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 20)
    private EntityType entityType;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /** Stored as String to bypass Hibernate timezone conversion on TIME columns */
    @Column(name = "start_time", columnDefinition = "TIME")
    private String startTime;

    /** Stored as String to bypass Hibernate timezone conversion on TIME columns */
    @Column(name = "end_time", columnDefinition = "TIME")
    private String endTime;

    @Column(length = 200)
    private String location;

    @Column(name = "color", length = 20)
    private String color;
}

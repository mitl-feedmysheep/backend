package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mitl.IntoTheHeaven.global.common.BaseEntity;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "`group`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupJpaEntity extends BaseEntity {

    private String name;

    private String description;

    @Column(name = "church_id", columnDefinition = "CHAR(36)")
    private UUID churchId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder
    public GroupJpaEntity(String name, String description, UUID churchId, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.description = description;
        this.churchId = churchId;
        this.startDate = startDate;
        this.endDate = endDate;
    }
} 
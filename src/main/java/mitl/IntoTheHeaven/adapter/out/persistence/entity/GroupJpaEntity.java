package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.common.BaseEntity;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`group`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@SQLRestriction("deleted_at is null")
public class GroupJpaEntity extends BaseEntity {

    /**
     * 그룹 이름
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * 설명
     */
    @Column(length = 100)
    private String description;

    /**
     * 교회
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "church_id", nullable = false)
    private ChurchJpaEntity church;

    /**
     * 시작 날짜
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * 종료 날짜
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @Builder.Default
    private List<GroupMemberJpaEntity> groupMembers = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @Builder.Default
    private List<GatheringJpaEntity> gatherings = new ArrayList<>();

    /**
     * 썸네일 미디어 (자주 사용되므로 Entity에 포함)
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "entity_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @SQLRestriction("entity_type = 'GROUP' AND media_type = 'THUMBNAIL'")
    @Builder.Default
    private List<MediaJpaEntity> thumbnails = new ArrayList<>();
} 
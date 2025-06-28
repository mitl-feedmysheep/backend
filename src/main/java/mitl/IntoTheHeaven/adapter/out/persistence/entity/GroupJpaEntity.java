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
@Table(name = "groups")
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
     * 로고 URL
     */
    @Column(name = "logo_url", length = 200)
    private String logoUrl;

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

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GroupMemberJpaEntity> groupMembers = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GatheringJpaEntity> gatherings = new ArrayList<>();
} 
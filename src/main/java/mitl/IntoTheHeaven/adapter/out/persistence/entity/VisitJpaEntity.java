package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.common.BaseEntity;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "visit")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@SQLRestriction("deleted_at is null")
public class VisitJpaEntity extends BaseEntity {

    /**
     * 교회
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "church_id", nullable = false)
    private ChurchJpaEntity church;

    /**
     * 심방 진행자 (목사님/리더)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "church_member_id", nullable = false)
    private ChurchMemberJpaEntity pastor;

    /**
     * 날짜
     */
    @Column(name = "date", nullable = false)
    private LocalDate date;

    /**
     * 시작 시간
     */
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    /**
     * 종료 시간
     */
    @Column(name = "ended_at", nullable = false)
    private LocalDateTime endedAt;

    /**
     * 장소
     */
    @Column(name = "place", length = 100, nullable = false)
    private String place;

    /**
     * 사용 금액
     */
    @Column(name = "expense", nullable = false)
    private Integer expense;

    /**
     * 설명
     */
    @Column(name = "notes", length = 500)
    private String notes;

    /**
     * 심방 멤버 목록
     */
    @OneToMany(mappedBy = "visit", cascade = CascadeType.ALL)
    @OrderBy("createdAt DESC")
    @Builder.Default
    private List<VisitMemberJpaEntity> visitMembers = new ArrayList<>();
}


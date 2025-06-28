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
@Table(name = "gathering")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@SQLRestriction("deleted_at is null")
public class GatheringJpaEntity extends BaseEntity {

    /**
     * 모임 이름
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * 설명
     */
    @Column(length = 100)
    private String description;

    /**
     * 날짜
     */
    @Column(nullable = false)
    private LocalDate date;

    /**
     * 시작 시간
     */
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    /**
     * 종료 시간
     */
    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    /**
     * 장소
     */
    @Column(length = 200)
    private String place;

    /**
     * 사진 URL
     */
    @Column(name = "photo_url", length = 200)
    private String photoUrl;

    /**
     * 리더 코멘트
     */
    @Column(name = "leader_comment", length = 100)
    private String leaderComment;

    /**
     * 관리자 코멘트
     */
    @Column(name = "admin_comment", length = 100)
    private String adminComment;

    /**
     * 그룹
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupJpaEntity group;

    @OneToMany(mappedBy = "gathering", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GatheringMemberJpaEntity> gatheringMembers = new ArrayList<>();
} 
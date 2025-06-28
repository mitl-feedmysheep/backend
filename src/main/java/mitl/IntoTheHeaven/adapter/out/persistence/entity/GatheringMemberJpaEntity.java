package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.common.BaseEntity;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gathering_member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@SQLRestriction("deleted_at is null")
public class GatheringMemberJpaEntity extends BaseEntity {

    /**
     * 모임
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id", nullable = false)
    private GatheringJpaEntity gathering;

    /**
     * 그룹 멤버
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_member_id", nullable = false)
    private GroupMemberJpaEntity groupMember;

    /**
     * 예배 참석 여부
     */
    @Column(name = "worship_attendance", nullable = false)
    private boolean worshipAttendance;

    /**
     * 모임 참석 여부
     */
    @Column(name = "gathering_attendance", nullable = false)
    private boolean gatheringAttendance;

    /**
     * 삶 나눔
     */
    @Column(length = 500)
    private String story;

    @OneToMany(mappedBy = "gatheringMember")
    private List<PrayerJpaEntity> prayers = new ArrayList<>();
} 
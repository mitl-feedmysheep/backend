package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.common.BaseEntity;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "prayer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@SQLRestriction("deleted_at is null")
public class PrayerJpaEntity extends BaseEntity {

    /**
     * 기도제목
     */
    @Column(name = "prayer_request", nullable = false, length = 200)
    private String prayerRequest;

    /**
     * 설명
     */
    @Column(length = 100)
    private String description;

    /**
     * 응답 여부
     */
    @Column(name = "is_answered", nullable = false)
    private boolean isAnswered;

    /**
     * 멤버 (개인 기도)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberJpaEntity member;

    /**
     * 모임 멤버 (모임 기도)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_member_id")
    private GatheringMemberJpaEntity gatheringMember;
} 
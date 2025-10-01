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

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "visit_member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@SQLRestriction("deleted_at is null")
public class VisitMemberJpaEntity extends BaseEntity {

    /**
     * 심방
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    private VisitJpaEntity visit;

    /**
     * 교회 멤버
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "church_member_id", nullable = false)
    private ChurchMemberJpaEntity churchMember;

    /**
     * 삶 나눔
     */
    @Column(name = "story", length = 500)
    private String story;

    /**
     * 기도 제목 목록
     */
    @OneToMany(mappedBy = "visitMember", cascade = CascadeType.ALL)
    @OrderBy("createdAt ASC")
    @Builder.Default
    private List<PrayerJpaEntity> prayers = new ArrayList<>();
}


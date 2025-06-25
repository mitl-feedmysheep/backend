package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "gathering_member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class GatheringMemberJpaEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id", nullable = false, columnDefinition = "CHAR(36)")
    private GatheringJpaEntity gathering;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_member_id", nullable = false, columnDefinition = "CHAR(36)")
    private GroupMemberJpaEntity groupMember;

    @Column(nullable = false)
    private Boolean worshipAttendance;

    @Column(nullable = false)
    private Boolean gatheringAttendance;

    @Column(columnDefinition = "TEXT")
    private String story;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @Builder
    public GatheringMemberJpaEntity(UUID id, GatheringJpaEntity gathering, GroupMemberJpaEntity groupMember, Boolean worshipAttendance, Boolean gatheringAttendance, String story, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.gathering = gathering;
        this.groupMember = groupMember;
        this.worshipAttendance = worshipAttendance;
        this.gatheringAttendance = gatheringAttendance;
        this.story = story;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }
} 
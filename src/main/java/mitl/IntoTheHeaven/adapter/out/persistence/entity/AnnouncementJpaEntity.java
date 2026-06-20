package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.common.BaseEntity;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "announcement",
        indexes = {
                @Index(name = "idx_announcement_entity", columnList = "entity_type, entity_id"),
                @Index(name = "idx_announcement_send_at_is_sent", columnList = "send_at, is_sent")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@SQLRestriction("deleted_at is null")
public class AnnouncementJpaEntity extends BaseEntity {

    @Column(name = "entity_type", nullable = false, length = 20)
    private String entityType;

    @Column(name = "entity_id", nullable = false, columnDefinition = "CHAR(36)")
    private String entityId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "send_at", nullable = false)
    private LocalDateTime sendAt;

    @Column(name = "is_sent", nullable = false)
    private boolean isSent;

    @Column(name = "push_enabled", nullable = false)
    private boolean pushEnabled;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    public void markAsSent() {
        this.isSent = true;
    }
}

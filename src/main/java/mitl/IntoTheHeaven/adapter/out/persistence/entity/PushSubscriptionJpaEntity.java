package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "push_subscription",
        indexes = @Index(name = "idx_push_subscription_member_id", columnList = "member_id")
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PushSubscriptionJpaEntity {

    @Id
    @Column(columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Column(name = "member_id", nullable = false, columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID memberId;

    @Column(name = "endpoint", nullable = false, length = 768, unique = true)
    private String endpoint;

    @Column(name = "p256dh", length = 255)
    private String p256dh;

    @Column(name = "auth", length = 255)
    private String auth;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "timezone", nullable = false, length = 64)
    private String timezone;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;
}

package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.domain.enums.PushType;
import mitl.IntoTheHeaven.global.common.BaseEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "push_delivery_log",
        indexes = {
                @Index(name = "idx_pdl_type_sent", columnList = "push_type, sent_at"),
                @Index(name = "idx_pdl_member_type_sent", columnList = "member_id, push_type, sent_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@SQLRestriction("deleted_at is null")
public class PushDeliveryLogJpaEntity extends BaseEntity {

    /** 수신 회원 ID */
    @Column(name = "member_id", nullable = false, columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID memberId;

    /** 발송 대상 push_subscription ID (구독 삭제 후에도 이력 유지를 위해 FK 없음) */
    @Column(name = "subscription_id", columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID subscriptionId;

    /** 알림 종류 */
    @Enumerated(EnumType.STRING)
    @Column(name = "push_type", nullable = false, length = 50)
    private PushType pushType;

    /** 사용한 문구 템플릿 키 */
    @Column(name = "template_key", length = 50)
    private String templateKey;

    /** 알림이 가리키는 엔티티 종류 */
    @Column(name = "entity_type", length = 30)
    private String entityType;

    /** 알림이 가리키는 엔티티 ID */
    @Column(name = "entity_id", columnDefinition = "CHAR(36)")
    private String entityId;

    /** 발송 결과 (SUCCESS/GONE/INVALID/TRANSIENT_FAIL) */
    @Column(name = "send_result", nullable = false, length = 20)
    private String sendResult;

    /** 발송 시각 (UTC) */
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    /** 최초 클릭 시각 (UTC) */
    @Column(name = "clicked_at")
    private LocalDateTime clickedAt;
}

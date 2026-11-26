package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.domain.enums.PushTopic;
import mitl.IntoTheHeaven.domain.enums.PushTopicAction;
import mitl.IntoTheHeaven.domain.enums.PushTopicChangeSource;
import mitl.IntoTheHeaven.global.common.BaseEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(
        name = "push_topic_history",
        indexes = {
                @Index(name = "idx_pth_topic_created", columnList = "topic, created_at"),
                @Index(name = "idx_pth_member_topic_created", columnList = "member_id, topic, created_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@SQLRestriction("deleted_at is null")
public class PushTopicHistoryJpaEntity extends BaseEntity {

    /** 회원 ID */
    @Column(name = "member_id", nullable = false, columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID memberId;

    /** 푸시 토픽 */
    @Enumerated(EnumType.STRING)
    @Column(name = "topic", nullable = false, length = 50)
    private PushTopic topic;

    /** 변경 종류 */
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 20)
    private PushTopicAction action;

    /** 변경 원인 */
    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 30)
    private PushTopicChangeSource source;
}

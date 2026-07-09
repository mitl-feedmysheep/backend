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
@Table(name = "report")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@SQLRestriction("deleted_at is null")
public class ReportJpaEntity extends BaseEntity {

    /**
     * 유형 (BUG/FEATURE_REQUEST/QUESTION)
     */
    @Column(name = "type", nullable = false, length = 20)
    private String type;

    /**
     * 최초 제출 내용
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 처리 상태 (RECEIVED/CONFIRMED/IN_PROGRESS/RESOLVED)
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /**
     * 작성자 (리포터)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private MemberJpaEntity reporter;

    public void changeStatus(String status) {
        this.status = status;
    }
}

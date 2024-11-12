package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import mitl.IntoTheHeaven.global.domain.BaseId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity<ID extends BaseId> {

  @Embedded
  protected ID id;

  @CreatedDate
  @Column(updatable = false, name = "created_at", nullable = true, columnDefinition = "datetime DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시'")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(updatable = false, name = "updated_at", nullable = true, columnDefinition = "datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시'")
  private LocalDateTime updatedAt;

  @Column(name = "deleted_at", nullable = true, columnDefinition = "datetime COMMENT '삭제일시'")
  private LocalDateTime deletedAt;

  protected BaseEntity() {
  }

  public void delete() {
    this.deletedAt = LocalDateTime.now();
  }
}

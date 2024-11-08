package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.global.domain.AggregateRoot;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "member")
@Getter
@Where(clause = "deleted_at IS NULL")
public class MemberEntity extends AggregateRoot<MemberEntity, MemberId> {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  // TODO 여기 작업하면 됨 ㅠ 어렵다 converter까지 써야할 듯;
  private MemberId id;

}

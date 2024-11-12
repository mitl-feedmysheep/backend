package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.enums.Gender;
import mitl.IntoTheHeaven.domain.model.id.MemberId;
import mitl.IntoTheHeaven.global.domain.AggregateRoot;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Where;

@Getter
@Entity
@Table(name = "member")
@Where(clause = "deleted_at IS NULL")
public class MemberEntity extends AggregateRoot<MemberEntity, MemberId> {

  @Comment("멤버 아이디")
  @EmbeddedId
  private MemberId id;

  @Comment("이름")
  @Column(name = "name", length = 20, nullable = false)
  private String name;

  @Comment("이메일")
  @Column(name = "email", length = 100, nullable = false)
  private String email;

  @Comment("비밀번호")
  @Column(name = "password", length = 100, nullable = false)
  private String password;

  @Comment("성별")
  @Enumerated(EnumType.STRING)
  @Column(name = "sex", length = 1, nullable = false)
  private Gender sex;

  @Comment("생년월일")
  @Column(name = "birthday", nullable = false)
  private LocalDate birthday;

  @Comment("휴대폰번호")
  @Column(name = "phone", length = 20, nullable = false)
  private String phone;

  @Comment("프로필 URL")
  @Column(name = "profile_url", length = 200)
  private String profileUrl;

  @Comment("주소")
  @Column(name = "address", length = 100)
  private String address;

  @Comment("특이사항")
  @Column(name = "description", length = 100)
  private String description;
}


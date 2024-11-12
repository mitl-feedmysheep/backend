package mitl.IntoTheHeaven.adapter.out.persistence.mapper;


import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberEntity;
import mitl.IntoTheHeaven.domain.model.Member;

public class MemberMapper {


  public static MemberEntity toEntity(Member member) {
    if (member == null) {
      return null;
    }

    return new MemberEntity();
  }

  public static Member toDomain(MemberEntity memberEntity) {
    if (memberEntity == null) {
      return null;
    }

    return Member.builder().build();
  }
}

package mitl.IntoTheHeaven.domain.model;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.enums.Gender;
import mitl.IntoTheHeaven.domain.model.id.MemberId;

@Getter
public class Member {

  private final MemberId id;
  private String name;
  private String email;
  private String password;
  private Gender sex;
  private LocalDate birthday;
  private String phone;
  private String profileUrl;
  private String address;
  private String description;

  @Builder()
  public Member(MemberId id, String name, String email, String password,
      Gender sex, LocalDate birthday, String phone,
      String profileUrl, String address, String description) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.sex = sex;
    this.birthday = birthday;
    this.phone = phone;
    this.profileUrl = profileUrl;
    this.address = address;
    this.description = description;
  }

  public static Member create(String name, String email, String password,
      Gender sex, LocalDate birthday, String phone,
      String profileUrl, String address, String description) {
    return Member.builder()
        .id(new MemberId())
        .name(name)
        .email(email)
        .password(password)
        .sex(sex)
        .birthday(birthday)
        .phone(phone)
        .profileUrl(profileUrl)
        .address(address)
        .description(description)
        .build();
  }
}

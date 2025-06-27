package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mitl.IntoTheHeaven.domain.enums.Gender;
import mitl.IntoTheHeaven.global.common.BaseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberJpaEntity extends BaseEntity {

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 1)
    private Gender sex;

    @Column(nullable = false)
    private LocalDate birthday;

    @Column(nullable = false, length = 20)
    private String phone;

    private String profileUrl;

    @Column(length = 100)
    private String address;

    @Column(length = 100)
    private String description;

    @OneToMany(mappedBy = "member")
    private List<GroupMemberJpaEntity> groupMembers = new ArrayList<>();


    @Builder
    public MemberJpaEntity(String name, String email, String password, Gender sex, LocalDate birthday, String phone, String profileUrl, String address, String description) {
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
}
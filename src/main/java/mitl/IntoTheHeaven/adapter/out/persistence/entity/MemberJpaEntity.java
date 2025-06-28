package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.domain.enums.Gender;
import mitl.IntoTheHeaven.global.common.BaseEntity;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@SQLRestriction("deleted_at is null")
public class MemberJpaEntity extends BaseEntity {

    /**
     * 이름
     */
    @Column(nullable = false, length = 20)
    private String name;

    /**
     * 이메일
     */
    @Column(nullable = false, length = 100)
    private String email;

    /**
     * 비밀번호
     */
    @Column(nullable = false, length = 100)
    private String password;

    /**
     * 성별
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 1)
    private Gender sex;

    /**
     * 생년월일
     */
    @Column(nullable = false)
    private LocalDate birthday;

    /**
     * 휴대폰번호
     */
    @Column(nullable = false, length = 20)
    private String phone;

    /**
     * 프로필 URL
     */
    @Column(name = "profile_url", length = 200)
    private String profile_url;

    /**
     * 주소
     */
    @Column(length = 100)
    private String address;

    /**
     * 특이사항
     */
    @Column(length = 100)
    private String description;

    @OneToMany(mappedBy = "member")
    private List<GroupMemberJpaEntity> groupMembers = new ArrayList<>();
}
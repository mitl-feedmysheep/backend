package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.common.BaseEntity;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "church")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@SQLRestriction("deleted_at is null")
public class ChurchJpaEntity extends BaseEntity {

    /**
     * 이름
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * 로고 URL
     */
    @Column(length = 200)
    private String logo_url;

    /**
     * 위치
     */
    @Column(nullable = false, length = 200)
    private String location;

    /**
     * 전화번호
     */
    @Column(length = 20)
    private String number;

    /**
     * 홈페이지 URL
     */
    @Column(length = 200)
    private String homepage_url;

    /**
     * 설명
     */
    @Column(length = 100)
    private String description;
} 
package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.domain.enums.VerificationType;
import mitl.IntoTheHeaven.global.common.BaseEntity;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "verification")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SQLRestriction("deleted_at is null")
public class VerificationJpaEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20, nullable = false)
    private VerificationType type;

    @Column(name = "type_value", length = 100, nullable = false)
    private String typeValue;

    @Column(name = "code", length = 10, nullable = false)
    private String code;
}



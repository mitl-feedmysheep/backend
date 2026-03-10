package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.MasterPasswordJpaEntity;
import mitl.IntoTheHeaven.domain.model.MasterPassword;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MasterPasswordPersistenceMapperTest {

    private final MasterPasswordPersistenceMapper mapper = new MasterPasswordPersistenceMapper();

    @Test
    @DisplayName("null 입력 시 null을 반환한다")
    void toDomain_nullInput() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("JPA Entity -> Domain 변환 시 id와 password가 정확하게 매핑된다")
    void toDomain() {
        UUID id = UUID.randomUUID();

        MasterPasswordJpaEntity entity = MasterPasswordJpaEntity.builder()
                .id(id)
                .password("$2a$10$hashedPassword123")
                .build();

        MasterPassword domain = mapper.toDomain(entity);

        assertThat(domain.getId().getValue()).isEqualTo(id);
        assertThat(domain.getPassword()).isEqualTo("$2a$10$hashedPassword123");
    }
}

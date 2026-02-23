package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.MasterPasswordJpaEntity;
import mitl.IntoTheHeaven.domain.model.MasterPassword;
import mitl.IntoTheHeaven.domain.model.MasterPasswordId;
import org.springframework.stereotype.Component;

@Component
public class MasterPasswordPersistenceMapper {

    public MasterPassword toDomain(MasterPasswordJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return MasterPassword.builder()
                .id(MasterPasswordId.from(entity.getId()))
                .password(entity.getPassword())
                .build();
    }
}

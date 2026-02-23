package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.MasterPasswordPersistenceMapper;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.MasterPasswordJpaRepository;
import mitl.IntoTheHeaven.application.port.out.MasterPasswordPort;
import mitl.IntoTheHeaven.domain.model.MasterPassword;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MasterPasswordPersistenceAdapter implements MasterPasswordPort {

    private final MasterPasswordJpaRepository masterPasswordJpaRepository;
    private final MasterPasswordPersistenceMapper masterPasswordPersistenceMapper;

    @Override
    public Optional<MasterPassword> findFirst() {
        return masterPasswordJpaRepository.findFirstBy()
                .map(masterPasswordPersistenceMapper::toDomain);
    }
}

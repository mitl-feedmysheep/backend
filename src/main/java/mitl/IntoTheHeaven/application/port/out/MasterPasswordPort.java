package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.MasterPassword;

import java.util.Optional;

public interface MasterPasswordPort {

    Optional<MasterPassword> findFirst();
}

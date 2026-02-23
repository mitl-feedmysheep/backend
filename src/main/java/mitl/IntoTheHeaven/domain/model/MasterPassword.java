package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

@Getter
@SuperBuilder(toBuilder = true)
public class MasterPassword extends DomainEntity<MasterPassword, MasterPasswordId> {

    private final String password;

    protected MasterPassword(MasterPasswordId id, String password) {
        super(id);
        this.password = password;
    }
}

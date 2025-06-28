package mitl.IntoTheHeaven.global.domain;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class AggregateRoot<T extends AggregateRoot<T, ID>, ID extends BaseId> extends DomainEntity<T, ID> {

    protected AggregateRoot(ID id) {
        super(id);
    }
}

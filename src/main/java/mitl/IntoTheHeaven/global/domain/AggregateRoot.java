package mitl.IntoTheHeaven.global.domain;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AggregateRoot<T extends DomainEntity<T, ID>, ID extends BaseId> extends
    DomainEntity<T, ID> {

  protected AggregateRoot() {
    super();
  }
}

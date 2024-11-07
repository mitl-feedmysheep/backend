package mitl.IntoTheHeaven.base.domain;

public abstract class AggregateRoot<T extends DomainEntity<T, TID>, TID> extends
    DomainEntity<T, TID> {

  public AggregateRoot() {
  }

}

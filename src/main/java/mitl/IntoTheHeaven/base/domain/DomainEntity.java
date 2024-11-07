package mitl.IntoTheHeaven.base.domain;

public abstract class DomainEntity<T extends DomainEntity<T, TID>, TID> {

  public DomainEntity() {
  }
}

package mitl.IntoTheHeaven.global.domain;

public abstract class AggregateRoot<T extends DomainEntity<T, ID>, ID extends BaseId> extends
    DomainEntity<T, ID> {

  protected AggregateRoot(ID id) {
    super(id);
  }
}

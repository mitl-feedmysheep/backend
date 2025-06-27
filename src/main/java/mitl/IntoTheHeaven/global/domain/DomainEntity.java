package mitl.IntoTheHeaven.global.domain;

import java.util.Objects;

public abstract class DomainEntity<T extends DomainEntity<T, ID>, ID extends BaseId> {

  private final ID id;

  protected DomainEntity(ID id) {
    this.id = id;
  }

  public ID getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DomainEntity<?, ?> that = (DomainEntity<?, ?>) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
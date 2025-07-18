package mitl.IntoTheHeaven.global.domain;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Getter
@SuperBuilder(toBuilder = true)
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
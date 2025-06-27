package mitl.IntoTheHeaven.global.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public abstract class BaseId implements Serializable {

  private UUID value;

  protected BaseId() {
    this.value = UUID.randomUUID();
  }

  protected BaseId(UUID value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BaseId baseId = (BaseId) o;
    return Objects.equals(value, baseId.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
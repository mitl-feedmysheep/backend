package mitl.IntoTheHeaven.global.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class BaseId implements Serializable {

  @Column(columnDefinition = "CHAR(36)")
  private final UUID value;

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
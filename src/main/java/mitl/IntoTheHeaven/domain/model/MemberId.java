package mitl.IntoTheHeaven.domain.model;

import java.util.UUID;
import lombok.Getter;
import mitl.IntoTheHeaven.global.domain.ValueObject;

@Getter
public class MemberId extends ValueObject<MemberId> {

  private final UUID id;

  public MemberId(UUID id) {
    this.id = id;
  }

  public static MemberId newId() {
    return new MemberId(UUID.randomUUID());
  }

  @Override
  protected Object[] getEqualityFields() {
    return new Object[]{id};
  }

}

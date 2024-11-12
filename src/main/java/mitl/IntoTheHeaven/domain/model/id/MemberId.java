package mitl.IntoTheHeaven.domain.model.id;

import jakarta.persistence.Embeddable;
import java.util.UUID;
import mitl.IntoTheHeaven.global.domain.BaseId;

@Embeddable
public class MemberId extends BaseId {

  protected MemberId() {
    super(UUID.randomUUID());
  }

  private MemberId(UUID value) {
    super(value);
  }

  public static MemberId of(UUID value) {
    return new MemberId(value);
  }
}

package mitl.IntoTheHeaven.domain.model;

import java.util.UUID;
import mitl.IntoTheHeaven.global.domain.BaseId;

public class MemberId extends BaseId {

  public MemberId(UUID value) {
    super(value);
  }

  public static MemberId newId() {
    return new MemberId(UUID.randomUUID());
  }
} 
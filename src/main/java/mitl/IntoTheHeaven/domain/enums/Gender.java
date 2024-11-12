package mitl.IntoTheHeaven.domain.enums;

import lombok.Getter;

@Getter
public enum Gender {
  M("남자"),
  F("여자");

  private final String description;

  Gender(String description) {
    this.description = description;
  }
}

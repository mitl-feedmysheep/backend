package mitl.IntoTheHeaven.global.domain;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.BaseEntity;

public abstract class DomainEntity<T extends DomainEntity<T, TID>, TID> extends BaseEntity {

  public DomainEntity() {
  }
}

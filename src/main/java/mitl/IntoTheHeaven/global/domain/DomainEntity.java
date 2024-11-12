package mitl.IntoTheHeaven.global.domain;

import jakarta.persistence.MappedSuperclass;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.BaseEntity;


@MappedSuperclass
public abstract class DomainEntity<T extends DomainEntity<T, ID>, ID extends BaseId> extends
    BaseEntity<ID> {

  protected DomainEntity() {
    super();
  }
}
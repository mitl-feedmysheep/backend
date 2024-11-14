package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.Member;

public interface MemberQueryUseCase {

  Member findByEmail(String email);
}

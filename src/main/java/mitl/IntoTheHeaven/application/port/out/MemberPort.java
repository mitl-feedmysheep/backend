package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.Member;

import java.util.Optional;
import java.util.UUID;

public interface MemberPort {

  Optional<Member> findById(UUID memberId);

  Member save(Member member);
} 
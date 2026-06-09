package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.PushSubscription;

import java.util.List;
import java.util.Optional;

public interface PushSubscriptionPort {

    PushSubscription save(PushSubscription pushSubscription);

    List<PushSubscription> findByMemberId(MemberId memberId);

    Optional<PushSubscription> findByEndpoint(String endpoint);

    boolean existsByEndpoint(String endpoint);

    List<PushSubscription> findAll();

    void deleteByEndpoint(String endpoint);
}

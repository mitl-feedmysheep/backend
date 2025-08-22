package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.Church;
import mitl.IntoTheHeaven.domain.model.MemberId;

import java.util.List;
import java.util.UUID;

public interface ChurchPort {
    List<Church> findChurchesByMemberId(UUID memberId);
    Church findById(UUID churchId);
    List<MemberId> findMemberIdsByChurchId(UUID churchId);
}
package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.enums.ChurchRole;
import mitl.IntoTheHeaven.domain.model.Church;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMember;
import mitl.IntoTheHeaven.domain.model.MemberId;

import java.util.List;
import java.util.UUID;

public interface ChurchPort {
    List<Church> findChurchesByMemberId(UUID memberId);

    Church findById(UUID churchId);

    List<MemberId> findMemberIdsByChurchId(UUID churchId);

    /* ADMIN */
    ChurchMember findChurchMemberByMemberIdAndChurchId(MemberId memberId, ChurchId churchId);

    List<ChurchMember> findChurchMembersByMemberIdAndRole(MemberId memberId, ChurchRole role);
}
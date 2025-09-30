package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.application.dto.MemberWithGroups;
import mitl.IntoTheHeaven.domain.model.Church;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.MemberId;

import java.util.List;

public interface ChurchQueryUseCase {
    List<Church> getChurchesByMemberId(MemberId memberId);

    /* ADMIN */
    List<Church> getAdminChurches(MemberId memberId);

    List<MemberWithGroups> searchChurchMembers(ChurchId churchId, String searchText);
}
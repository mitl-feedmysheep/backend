package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.application.dto.MemberWithGroups;
import mitl.IntoTheHeaven.domain.enums.ChurchRole;
import mitl.IntoTheHeaven.domain.model.Church;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMemberRequest;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;

import java.util.List;

public interface ChurchQueryUseCase {
    List<Church> getChurchesByMemberId(MemberId memberId);

    List<Church> getAllChurches();

    List<ChurchMemberRequest> getMyJoinRequests(MemberId memberId);

    List<Member> getBirthdayMembers(ChurchId churchId, int month);

    boolean hasElevatedSearchAccess(MemberId memberId, ChurchId churchId);

    /* ADMIN */
    List<Church> getAdminChurches(MemberId memberId);

    ChurchRole getCurrentRole(MemberId memberId, ChurchId churchId);

    List<MemberWithGroups> searchChurchMembers(ChurchId churchId, String searchText);
}
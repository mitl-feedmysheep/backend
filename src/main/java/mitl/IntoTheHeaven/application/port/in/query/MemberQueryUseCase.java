package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;

import java.util.List;
import java.util.UUID;

import mitl.IntoTheHeaven.application.port.in.query.dto.AdminMeResponse;
import mitl.IntoTheHeaven.domain.model.ChurchId;

public interface MemberQueryUseCase {
    Member getMemberById(MemberId memberId);
    List<Member> getMembersByGroupId(UUID groupId);

    /**
     * ADMIN - Get admin user info in specific church context
     */
    AdminMeResponse getAdminMyInfo(MemberId memberId, ChurchId churchId);

    /**
     * Checks availability of a phone number for signup.
     * @param phone phone number string
     * @return true if the phone number is not used yet
     */
    boolean isPhoneAvailable(String phone);

    /**
     * Checks availability of an email for signup.
     * @param email email string
     * @return true if the email is not used yet
     */
    boolean isEmailAvailable(String email);

    /**
     * Verifies if a member exists with the given email and name combination.
     * @param email member's email
     * @param name member's name
     * @return true if a member with the given email and name exists
     */
    boolean verifyMemberByEmailAndName(String email, String name);
} 
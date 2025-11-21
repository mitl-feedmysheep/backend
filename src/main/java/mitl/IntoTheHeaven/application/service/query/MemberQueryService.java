package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.query.MemberQueryUseCase;
import mitl.IntoTheHeaven.application.port.in.query.dto.AdminMeResponse;
import mitl.IntoTheHeaven.application.port.out.MemberPort;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.application.port.out.ChurchPort;
import mitl.IntoTheHeaven.domain.enums.ChurchRole;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService implements MemberQueryUseCase {

  private final MemberPort memberPort;
  private final ChurchPort churchPort;

  @Override
  public Member getMemberById(MemberId memberId) {
    return memberPort.findById(memberId.getValue())
        .orElseThrow(() -> new RuntimeException("Member not found"));
  }

  @Override
  public List<Member> getMembersByGroupId(UUID groupId) {
    return memberPort.findMembersByGroupId(groupId);
  }

  @Override
  public AdminMeResponse getAdminMyInfo(MemberId memberId, ChurchId churchId) {
    ChurchRole role = churchPort.findChurchMemberByMemberIdAndChurchId(memberId, churchId)
        .getRole();
    return AdminMeResponse.from(role);
  }

  @Override
  public boolean isPhoneAvailable(String phone) {
    return memberPort.findByPhone(phone).isEmpty();
  }

  @Override
  public boolean isEmailAvailable(String email) {
    return memberPort.findByEmail(email).isEmpty();
  }

  @Override
  public boolean verifyMemberByEmailAndName(String email, String name) {
    if (memberPort.findByEmailAndName(email, name).isPresent()) {
      return true;
    }
    return false;
  }
}
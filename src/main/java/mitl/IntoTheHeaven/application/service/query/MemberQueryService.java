package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.query.MemberQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.MemberPort;
import mitl.IntoTheHeaven.domain.model.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService implements MemberQueryUseCase {

  private final MemberPort memberPort;

  @Override
  public Member getMemberById(UUID memberId) {
    return memberPort.findById(memberId)
            .orElseThrow(() -> new RuntimeException("Member not found"));
  }

  @Override
  public List<Member> getMembersByGroupId(UUID groupId) {
    return memberPort.findMembersByGroupId(groupId);
  }
} 
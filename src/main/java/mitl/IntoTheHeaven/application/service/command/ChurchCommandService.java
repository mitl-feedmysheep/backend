package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.ChurchCommandUseCase;
import mitl.IntoTheHeaven.application.port.out.ChurchPort;
import mitl.IntoTheHeaven.domain.enums.RequestStatus;
import mitl.IntoTheHeaven.domain.model.Church;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMemberRequest;
import mitl.IntoTheHeaven.domain.model.ChurchMemberRequestId;
import mitl.IntoTheHeaven.domain.model.DepartmentId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ChurchCommandService implements ChurchCommandUseCase {

    private final ChurchPort churchPort;

    @Override
    public ChurchMemberRequest createJoinRequest(MemberId memberId, ChurchId churchId, DepartmentId departmentId) {
        Church church = churchPort.findById(churchId.getValue());
        if (church == null) {
            throw new IllegalArgumentException("Church not found");
        }

        churchPort.findPendingJoinRequest(memberId.getValue(), churchId.getValue())
                .ifPresent(existing -> {
                    throw new IllegalStateException("A pending join request already exists for this church");
                });

        ChurchMemberRequest request = ChurchMemberRequest.builder()
                .id(ChurchMemberRequestId.from(UUID.randomUUID()))
                .memberId(memberId)
                .churchId(churchId)
                .departmentId(departmentId)
                .status(RequestStatus.PENDING)
                .churchName(church.getName())
                .build();

        return churchPort.saveJoinRequest(request);
    }
}

package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mitl.IntoTheHeaven.application.port.in.command.ChurchCommandUseCase;
import mitl.IntoTheHeaven.application.port.out.ChurchPort;
import mitl.IntoTheHeaven.application.port.out.DepartmentPort;
import mitl.IntoTheHeaven.application.port.out.MemberPort;
import mitl.IntoTheHeaven.application.port.out.NotificationPort;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort.PushPayload;
import mitl.IntoTheHeaven.domain.enums.ChurchRole;
import mitl.IntoTheHeaven.domain.enums.NotificationType;
import mitl.IntoTheHeaven.domain.model.Church;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMemberRequest;
import mitl.IntoTheHeaven.domain.model.ChurchMemberRequestId;
import mitl.IntoTheHeaven.domain.model.Department;
import mitl.IntoTheHeaven.domain.model.DepartmentId;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Notification;
import mitl.IntoTheHeaven.domain.model.NotificationId;
import mitl.IntoTheHeaven.domain.model.PushSubscription;
import mitl.IntoTheHeaven.domain.enums.RequestStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChurchCommandService implements ChurchCommandUseCase {

    private final ChurchPort churchPort;
    private final DepartmentPort departmentPort;
    private final MemberPort memberPort;
    private final NotificationPort notificationPort;
    private final PushSubscriptionPort pushSubscriptionPort;
    private final WebPushPort webPushPort;

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

        ChurchMemberRequest saved = churchPort.saveJoinRequest(request);

        notifyAdmins(saved, memberId, churchId, departmentId);

        return saved;
    }

    private void notifyAdmins(ChurchMemberRequest request, MemberId memberId, ChurchId churchId, DepartmentId departmentId) {
        try {
            String memberName = memberPort.findById(memberId.getValue())
                    .map(Member::getName)
                    .orElse("알 수 없음");

            List<MemberId> targets;
            String deptName;

            if (departmentId != null) {
                deptName = departmentPort.findById(departmentId.getValue())
                        .map(Department::getName)
                        .orElse("부서 미지정");
                targets = departmentPort.findAdminsByDepartmentId(departmentId.getValue());
            } else {
                deptName = "부서 미지정";
                targets = churchPort.findMemberIdsByChurchIdAndRole(churchId.getValue(), ChurchRole.SUPER_ADMIN);
            }

            if (targets.isEmpty()) return;

            String body = memberName + "님이 " + deptName + " 가입을 신청했어요. 어드민에서 확인해주세요.";
            String requestId = request.getId().getValue().toString();

            for (MemberId targetId : targets) {
                boolean alreadyExists = notificationPort.existsUnreadByReceiverAndTypeAndEntity(
                        targetId.getValue(),
                        NotificationType.JOIN_REQUEST.getValue(),
                        "CHURCH_MEMBER_REQUEST",
                        requestId);

                if (!alreadyExists) {
                    notificationPort.save(Notification.builder()
                            .id(NotificationId.from(UUID.randomUUID()))
                            .receiverId(targetId)
                            .senderId(memberId)
                            .departmentId(departmentId)
                            .type(NotificationType.JOIN_REQUEST)
                            .description(body)
                            .entityType("CHURCH_MEMBER_REQUEST")
                            .entityId(requestId)
                            .targetUrl("/notifications")
                            .isRead(false)
                            .build());
                }
            }

            List<PushSubscription> subscriptions = pushSubscriptionPort.findByMemberIds(targets);
            PushPayload payload = new PushPayload("새 편입 요청", body, "/notifications");
            for (PushSubscription sub : subscriptions) {
                WebPushPort.SendResult result = webPushPort.send(sub, payload);
                if (result == WebPushPort.SendResult.GONE || result == WebPushPort.SendResult.INVALID) {
                    pushSubscriptionPort.deleteByEndpoint(sub.getEndpoint());
                }
            }
        } catch (Exception e) {
            log.error("Failed to notify admins for join request: {}", e.getMessage());
        }
    }
}

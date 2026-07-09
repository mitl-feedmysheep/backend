package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mitl.IntoTheHeaven.application.port.in.command.ReportCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.CreateReportCommand;
import mitl.IntoTheHeaven.application.port.out.NotificationPort;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionPort;
import mitl.IntoTheHeaven.application.port.out.ReportCommentPort;
import mitl.IntoTheHeaven.application.port.out.ReportPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort.PushPayload;
import mitl.IntoTheHeaven.domain.enums.NotificationType;
import mitl.IntoTheHeaven.domain.enums.ReportStatus;
import mitl.IntoTheHeaven.domain.enums.ReportType;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Notification;
import mitl.IntoTheHeaven.domain.model.NotificationId;
import mitl.IntoTheHeaven.domain.model.PushSubscription;
import mitl.IntoTheHeaven.domain.model.Report;
import mitl.IntoTheHeaven.domain.model.ReportComment;
import mitl.IntoTheHeaven.domain.model.ReportCommentId;
import mitl.IntoTheHeaven.domain.model.ReportId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReportCommandService implements ReportCommandUseCase {

    private final ReportPort reportPort;
    private final ReportCommentPort reportCommentPort;
    private final NotificationPort notificationPort;
    private final PushSubscriptionPort pushSubscriptionPort;
    private final WebPushPort webPushPort;

    @Value("${report.system-admin-member-id}")
    private String systemAdminMemberId;

    private static final Map<ReportType, String> REPORT_CREATED_TITLE = Map.of(
            ReportType.BUG, "버그가 접수되었어요 🐛",
            ReportType.FEATURE_REQUEST, "기능 요청이 접수되었어요 🆕",
            ReportType.QUESTION, "질문이 접수되었어요 🙋‍♂️");

    @Override
    public ReportId create(CreateReportCommand command) {
        Report report = Report.builder()
                .id(ReportId.from(UUID.randomUUID()))
                .reporterId(command.getReporterId())
                .type(command.getType())
                .content(command.getContent())
                .status(ReportStatus.RECEIVED)
                .build();
        Report saved = reportPort.save(report);

        if (!isSystemAdmin(command.getReporterId())) {
            notifyAndPush(
                    MemberId.from(UUID.fromString(systemAdminMemberId)),
                    command.getReporterId(),
                    NotificationType.REPORT_CREATED,
                    REPORT_CREATED_TITLE.get(command.getType()),
                    null,
                    saved.getId());
        }

        return saved.getId();
    }

    @Override
    public void addComment(MemberId callerId, ReportId reportId, String content) {
        Report report = reportPort.findById(reportId.getValue())
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        boolean callerIsReporter = report.getReporterId().equals(callerId);
        boolean callerIsAdmin = isSystemAdmin(callerId);
        if (!callerIsReporter && !callerIsAdmin) {
            throw new AccessDeniedException("Only the reporter or admin can comment on this report");
        }

        ReportComment comment = ReportComment.builder()
                .id(ReportCommentId.from(UUID.randomUUID()))
                .reportId(reportId)
                .authorId(callerId)
                .content(content)
                .build();
        reportCommentPort.save(comment);

        MemberId counterpart = callerIsAdmin
                ? report.getReporterId()
                : MemberId.from(UUID.fromString(systemAdminMemberId));

        if (!counterpart.equals(callerId)) {
            String body = truncate(content);
            notifyAndPush(counterpart, callerId, NotificationType.REPORT_COMMENT_ADDED, "리포트에 답변이 달렸어요", body, reportId);
        }
    }

    @Override
    public void updateStatus(MemberId callerId, ReportId reportId, ReportStatus status) {
        if (!isSystemAdmin(callerId)) {
            throw new AccessDeniedException("Only the system admin can change report status");
        }
        reportPort.findById(reportId.getValue())
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        reportPort.updateStatus(reportId.getValue(), status);
    }

    private boolean isSystemAdmin(MemberId memberId) {
        return memberId.getValue().toString().equals(systemAdminMemberId);
    }

    private void notifyAndPush(MemberId receiverId, MemberId senderId, NotificationType type, String title, String body, ReportId reportId) {
        String targetUrl = "/my/report/" + reportId.getValue();

        notificationPort.save(Notification.builder()
                .id(NotificationId.from(UUID.randomUUID()))
                .receiverId(receiverId)
                .senderId(senderId)
                .type(type)
                .description(body)
                .entityType("REPORT")
                .entityId(reportId.getValue().toString())
                .targetUrl(targetUrl)
                .isRead(false)
                .build());

        try {
            List<PushSubscription> subscriptions = pushSubscriptionPort.findByMemberId(receiverId);
            PushPayload payload = new PushPayload(title, body, targetUrl);
            for (PushSubscription sub : subscriptions) {
                WebPushPort.SendResult result = webPushPort.send(sub, payload);
                if (result == WebPushPort.SendResult.GONE || result == WebPushPort.SendResult.INVALID) {
                    pushSubscriptionPort.deleteByEndpoint(sub.getEndpoint());
                }
            }
        } catch (Exception e) {
            log.error("Failed to send report push to member {}: {}", receiverId.getValue(), e.getMessage());
        }
    }

    private String truncate(String content) {
        if (content == null) return "";
        return content.length() > 200 ? content.substring(0, 200) + "..." : content;
    }
}

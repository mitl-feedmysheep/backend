package mitl.IntoTheHeaven.global.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.DepartmentMemberJpaRepository;
import mitl.IntoTheHeaven.application.port.out.AnnouncementPort;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort.PushPayload;
import mitl.IntoTheHeaven.application.port.out.WebPushPort.SendResult;
import mitl.IntoTheHeaven.domain.model.Announcement;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.PushSubscription;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "webpush.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class AnnouncementPushScheduler {

    private static final int PREVIEW_MAX_LENGTH = 50;

    private final AnnouncementPort announcementPort;
    private final DepartmentMemberJpaRepository departmentMemberJpaRepository;
    private final PushSubscriptionPort pushSubscriptionPort;
    private final WebPushPort webPushPort;

    @Scheduled(cron = "0 */5 * * * *")
    public void sendScheduledAnnouncements() {
        List<Announcement> pending = announcementPort.findPendingToSend(LocalDateTime.now());
        if (pending.isEmpty()) return;

        log.info("Announcement push scheduler: {} pending announcement(s)", pending.size());

        for (Announcement announcement : pending) {
            try {
                if (announcement.isPushEnabled()) {
                    sendAnnouncement(announcement);
                }
                announcementPort.markAsSent(announcement.getId().getValue());
            } catch (Exception e) {
                log.error("Failed to send announcement {}: {}", announcement.getId().getValue(), e.getMessage());
            }
        }
    }

    private void sendAnnouncement(Announcement announcement) {
        // DEPARTMENT 기준으로 소속 멤버 UUID 목록 조회
        UUID departmentId = UUID.fromString(announcement.getEntityId());
        List<MemberId> memberIds = departmentMemberJpaRepository
                .findByDepartmentId(departmentId)
                .stream()
                .map(dm -> MemberId.from(dm.getMember().getId()))
                .toList();

        if (memberIds.isEmpty()) return;

        List<PushSubscription> subscriptions = pushSubscriptionPort.findByMemberIds(memberIds);
        if (subscriptions.isEmpty()) return;

        PushPayload payload = buildPayload(announcement);

        for (PushSubscription sub : subscriptions) {
            SendResult result = webPushPort.send(sub, payload);
            if (result == SendResult.GONE || result == SendResult.INVALID) {
                log.info("Removing expired subscription: {}", sub.getEndpoint());
                pushSubscriptionPort.deleteByEndpoint(sub.getEndpoint());
            } else if (result == SendResult.TRANSIENT_FAIL) {
                log.warn("Transient push failure for announcement {}: {}", announcement.getId().getValue(), sub.getEndpoint());
            }
        }

        log.info("Announcement {} sent to {} subscription(s)", announcement.getId().getValue(), subscriptions.size());
    }

    private PushPayload buildPayload(Announcement announcement) {
        String body = announcement.getBody();
        String preview = body.length() > PREVIEW_MAX_LENGTH
                ? body.substring(0, PREVIEW_MAX_LENGTH) + "..."
                : body;
        String url = "/announcements/" + announcement.getId().getValue();
        return new PushPayload(announcement.getTitle(), preview, url);
    }
}

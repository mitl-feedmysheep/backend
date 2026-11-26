package mitl.IntoTheHeaven.global.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.DepartmentMemberJpaRepository;
import mitl.IntoTheHeaven.application.port.out.AnnouncementPort;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionPort;
import mitl.IntoTheHeaven.application.service.command.PushDispatchService;
import mitl.IntoTheHeaven.application.service.command.PushDispatchService.PushMessage;
import mitl.IntoTheHeaven.domain.enums.PushType;
import mitl.IntoTheHeaven.domain.model.Announcement;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.PushSubscription;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    private final PushDispatchService pushDispatchService;

    @Scheduled(cron = "0 */5 * * * *")
    public void sendScheduledAnnouncements() {
        // send_at은 hibernate.jdbc.time_zone=UTC 설정으로 UTC 인스턴트가 그대로 저장되므로,
        // 비교 기준도 반드시 UTC여야 함 (KST로 바꾸면 9시간 일찍 발송됨)
        List<Announcement> pending = announcementPort.findPendingToSend(LocalDateTime.now(ZoneOffset.UTC));
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

        pushDispatchService.sendToSubscriptions(subscriptions, buildMessage(announcement));

        log.info("Announcement {} sent to {} subscription(s)", announcement.getId().getValue(), subscriptions.size());
    }

    private PushMessage buildMessage(Announcement announcement) {
        String body = announcement.getBody() != null ? announcement.getBody() : announcement.getTitle();
        String preview = body.length() > PREVIEW_MAX_LENGTH
                ? body.substring(0, PREVIEW_MAX_LENGTH) + "..."
                : body;
        String url = "/announcements/" + announcement.getId().getValue();
        PushType type = "PUSH_BROADCAST".equals(announcement.getEntityType())
                ? PushType.PUSH_BROADCAST
                : PushType.ANNOUNCEMENT;
        return PushMessage.of(type, announcement.getTitle(), preview, url)
                .withEntity("ANNOUNCEMENT", announcement.getId().getValue().toString());
    }
}

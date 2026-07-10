package mitl.IntoTheHeaven.global.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mitl.IntoTheHeaven.application.service.command.PushNotificationSendService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "webpush.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class DailyPrayerPushScheduler {

    private final PushNotificationSendService pushNotificationSendService;

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void sendPrayerPush() {
        log.info("Daily prayer push scheduler triggered");
        pushNotificationSendService.sendDailyPrayerPush();
    }
}

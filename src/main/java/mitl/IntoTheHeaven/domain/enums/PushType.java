package mitl.IntoTheHeaven.domain.enums;

/**
 * 푸시 알림 종류 (발송 로그 집계 및 서비스워커 tag 결정에 사용)
 */
public enum PushType {
    DAILY_PRAYER,
    DAILY_READING,
    ANNOUNCEMENT,
    PUSH_BROADCAST,
    LEADER_COMMENT,
    REPORT_CREATED,
    REPORT_COMMENT_ADDED,
    JOIN_REQUEST,
    /** admin(Next.js)에서 직접 발송 — 목회자 코멘트 */
    ADMIN_COMMENT
}

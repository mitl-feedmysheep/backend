package mitl.IntoTheHeaven.domain.enums;

public enum PushTopicChangeSource {
    /** 알림 설정 화면에서 사용자가 직접 토글 */
    USER_TOGGLE,
    /** 회원의 마지막 기기 구독이 해지되어 토픽이 함께 정리됨 */
    ALL_DEVICES_REMOVED
}

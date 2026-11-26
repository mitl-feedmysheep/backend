package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.PrayerId;

public interface PrayerCommandUseCase {

    /** 나의 기도(모임/심방과 무관한 개인 기도제목) 작성 */
    PrayerId createPersonal(MemberId memberId, String prayerRequest);

    /** 나의 기도 내용 수정 (본인 + 나의 기도만) */
    void updateContent(MemberId callerId, PrayerId prayerId, String prayerRequest);

    /** 삭제. 나의 기도는 본인만 삭제 가능 */
    void delete(MemberId callerId, PrayerId prayerId);

    /** 응답 여부 변경 (본인만) */
    void updateAnswered(MemberId callerId, PrayerId prayerId, boolean isAnswered);
}

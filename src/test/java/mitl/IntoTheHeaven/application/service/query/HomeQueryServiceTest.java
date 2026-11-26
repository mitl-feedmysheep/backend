package mitl.IntoTheHeaven.application.service.query;

import mitl.IntoTheHeaven.adapter.in.web.dto.home.HomeGoalResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.home.HomeMyPrayerResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.home.HomePrayerResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.home.HomeSummaryResponse;
import mitl.IntoTheHeaven.application.port.out.GatheringPort;
import mitl.IntoTheHeaven.application.port.out.HomeSummaryData;
import mitl.IntoTheHeaven.application.port.out.PrayerPort;
import mitl.IntoTheHeaven.domain.model.GatheringMemberId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Prayer;
import mitl.IntoTheHeaven.domain.model.PrayerId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HomeQueryServiceTest {

    @Mock
    private GatheringPort gatheringPort;

    @Mock
    private PrayerPort prayerPort;

    @InjectMocks
    private HomeQueryService homeQueryService;

    private static Prayer myPrayer(MemberId memberId, String text, boolean answered, LocalDateTime createdAt) {
        return Prayer.builder()
                .id(PrayerId.from(UUID.randomUUID()))
                .memberId(memberId)
                .prayerRequest(text)
                .isAnswered(answered)
                .createdAt(createdAt)
                .build();
    }

    @Nested
    @DisplayName("getHomeSummary - 홈 요약 조회")
    class GetHomeSummary {

        @Test
        @DisplayName("최근 7일 모임 데이터가 없으면 빈 목록 반환")
        void shouldReturnEmptyWhenNoRecentGatherings() {
            MemberId memberId = MemberId.from(UUID.randomUUID());

            when(gatheringPort.findRecentGatheringMemberData(eq(memberId.getValue()), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(List.of());

            HomeSummaryResponse result = homeQueryService.getHomeSummary(memberId);

            assertThat(result.getGoals()).isEmpty();
            assertThat(result.getPrayers()).isEmpty();
            assertThat(result.getMyPrayers()).isEmpty();
            assertThat(result.getMyPrayerCount()).isZero();
            verify(prayerPort, never()).findByGatheringMemberIds(any());
        }

        @Test
        @DisplayName("모임이 없어도 나의 기도는 반환한다 (미응답만, 최근순 최대 3개 + 전체 개수)")
        void shouldReturnMyPrayersEvenWithoutGatherings() {
            MemberId memberId = MemberId.from(UUID.randomUUID());
            when(gatheringPort.findRecentGatheringMemberData(eq(memberId.getValue()), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(List.of());

            LocalDateTime now = LocalDateTime.now();
            Prayer my1 = myPrayer(memberId, "나의 기도 1", false, now);
            Prayer my2 = myPrayer(memberId, "나의 기도 2", false, now.minusDays(1));
            Prayer answered = myPrayer(memberId, "응답됨", true, now.minusDays(2));
            Prayer my3 = myPrayer(memberId, "나의 기도 3", false, now.minusDays(3));
            Prayer my4 = myPrayer(memberId, "나의 기도 4", false, now.minusDays(4));
            Prayer gatheringPrayer = myPrayer(memberId, "모임 기도", false, now).toBuilder()
                    .gatheringMemberId(GatheringMemberId.from(UUID.randomUUID()))
                    .build();
            when(prayerPort.findAllByMemberId(memberId.getValue()))
                    .thenReturn(List.of(my1, gatheringPrayer, my2, answered, my3, my4));

            HomeSummaryResponse result = homeQueryService.getHomeSummary(memberId);

            assertThat(result.getMyPrayers())
                    .extracting(HomeMyPrayerResponse::getPrayerRequest)
                    .containsExactly("나의 기도 1", "나의 기도 2", "나의 기도 3");
            assertThat(result.getMyPrayerCount()).isEqualTo(4);
        }

        @Test
        @DisplayName("목표와 기도제목을 정상 반환")
        void shouldReturnGoalsAndPrayers() {
            MemberId memberId = MemberId.from(UUID.randomUUID());
            UUID gmId1 = UUID.randomUUID();
            UUID gmId2 = UUID.randomUUID();

            List<HomeSummaryData> data = List.of(
                    new HomeSummaryData(gmId1, "매일 큐티하기", LocalDate.now(), "청년1셀"),
                    new HomeSummaryData(gmId2, "새벽기도 참석", LocalDate.now().minusDays(3), "청년2셀")
            );

            List<Prayer> prayers = List.of(
                    Prayer.builder()
                            .id(PrayerId.from(UUID.randomUUID()))
                            .memberId(memberId)
                            .gatheringMemberId(GatheringMemberId.from(gmId1))
                            .prayerRequest("취업을 위해")
                            .description("IT 회사")
                            .isAnswered(false)
                            .build()
            );

            when(gatheringPort.findRecentGatheringMemberData(eq(memberId.getValue()), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(data);
            when(prayerPort.findByGatheringMemberIds(List.of(gmId1, gmId2)))
                    .thenReturn(prayers);

            HomeSummaryResponse result = homeQueryService.getHomeSummary(memberId);

            assertThat(result.getGoals()).hasSize(2);
            assertThat(result.getGoals().get(0).getGoal()).isEqualTo("매일 큐티하기");
            assertThat(result.getGoals().get(0).getGroupName()).isEqualTo("청년1셀");
            assertThat(result.getGoals().get(1).getGoal()).isEqualTo("새벽기도 참석");

            assertThat(result.getPrayers()).hasSize(1);
            assertThat(result.getPrayers().get(0).getPrayerRequest()).isEqualTo("취업을 위해");
            assertThat(result.getPrayers().get(0).getGroupName()).isEqualTo("청년1셀");
            assertThat(result.getPrayers().get(0).isAnswered()).isFalse();
        }

        @Test
        @DisplayName("null/blank 목표는 필터링")
        void shouldFilterNullAndBlankGoals() {
            MemberId memberId = MemberId.from(UUID.randomUUID());
            UUID gmId1 = UUID.randomUUID();
            UUID gmId2 = UUID.randomUUID();
            UUID gmId3 = UUID.randomUUID();

            List<HomeSummaryData> data = List.of(
                    new HomeSummaryData(gmId1, "유효한 목표", LocalDate.now(), "셀1"),
                    new HomeSummaryData(gmId2, null, LocalDate.now(), "셀2"),
                    new HomeSummaryData(gmId3, "  ", LocalDate.now(), "셀3")
            );

            when(gatheringPort.findRecentGatheringMemberData(eq(memberId.getValue()), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(data);
            when(prayerPort.findByGatheringMemberIds(any())).thenReturn(List.of());

            HomeSummaryResponse result = homeQueryService.getHomeSummary(memberId);

            assertThat(result.getGoals()).hasSize(1);
            assertThat(result.getGoals().get(0).getGoal()).isEqualTo("유효한 목표");
        }

        @Test
        @DisplayName("null/blank 기도제목은 필터링")
        void shouldFilterNullAndBlankPrayers() {
            MemberId memberId = MemberId.from(UUID.randomUUID());
            UUID gmId = UUID.randomUUID();

            List<HomeSummaryData> data = List.of(
                    new HomeSummaryData(gmId, null, LocalDate.now(), "셀1")
            );

            List<Prayer> prayers = List.of(
                    Prayer.builder()
                            .id(PrayerId.from(UUID.randomUUID()))
                            .memberId(memberId)
                            .gatheringMemberId(GatheringMemberId.from(gmId))
                            .prayerRequest("유효한 기도제목")
                            .isAnswered(false)
                            .build(),
                    Prayer.builder()
                            .id(PrayerId.from(UUID.randomUUID()))
                            .memberId(memberId)
                            .gatheringMemberId(GatheringMemberId.from(gmId))
                            .prayerRequest("")
                            .isAnswered(false)
                            .build()
            );

            when(gatheringPort.findRecentGatheringMemberData(eq(memberId.getValue()), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(data);
            when(prayerPort.findByGatheringMemberIds(List.of(gmId))).thenReturn(prayers);

            HomeSummaryResponse result = homeQueryService.getHomeSummary(memberId);

            assertThat(result.getPrayers()).hasSize(1);
            assertThat(result.getPrayers().get(0).getPrayerRequest()).isEqualTo("유효한 기도제목");
        }
    }
}

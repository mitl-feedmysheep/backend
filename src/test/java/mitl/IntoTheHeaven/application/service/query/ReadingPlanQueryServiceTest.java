package mitl.IntoTheHeaven.application.service.query;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.DepartmentMemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.DepartmentReadingPlanJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.ReadingCompletionHistoryJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.ReadingPlanJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.DepartmentMemberJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.DepartmentReadingPlanJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.ReadingCompletionHistoryJpaRepository;
import mitl.IntoTheHeaven.application.port.in.query.ReadingPlanQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.ReadingPlanPort;
import mitl.IntoTheHeaven.domain.enums.DepartmentMemberStatus;
import mitl.IntoTheHeaven.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadingPlanQueryServiceTest {

    @Mock
    private ReadingPlanPort readingPlanPort;

    @Mock
    private DepartmentReadingPlanJpaRepository departmentReadingPlanJpaRepository;

    @Mock
    private ReadingCompletionHistoryJpaRepository readingCompletionHistoryJpaRepository;

    @Mock
    private DepartmentMemberJpaRepository departmentMemberJpaRepository;

    @InjectMocks
    private ReadingPlanQueryService service;

    private UUID deptUuid;
    private UUID deptPlanUuid;
    private UUID planUuid;
    private DepartmentId departmentId;
    private MemberId memberId;

    @BeforeEach
    void setUp() {
        deptUuid = UUID.randomUUID();
        deptPlanUuid = UUID.randomUUID();
        planUuid = UUID.randomUUID();
        departmentId = DepartmentId.from(deptUuid);
        memberId = MemberId.from(UUID.randomUUID());
    }

    /** lenient() 사용: 특정 테스트에서 plan.getId()나 getTotalDays()가 불필요해도 예외 방지 */
    private DepartmentReadingPlanJpaEntity mockMappingEntity(int totalDays) {
        ReadingPlanJpaEntity plan = mock(ReadingPlanJpaEntity.class);
        lenient().when(plan.getId()).thenReturn(planUuid);
        lenient().when(plan.getTotalDays()).thenReturn(totalDays);

        DepartmentReadingPlanJpaEntity mapping = mock(DepartmentReadingPlanJpaEntity.class);
        lenient().when(mapping.getId()).thenReturn(deptPlanUuid);
        lenient().when(mapping.getReadingPlan()).thenReturn(plan);
        return mapping;
    }

    private ReadingCompletionHistoryJpaEntity mockCompletion(LocalDateTime completedAt) {
        ReadingCompletionHistoryJpaEntity entity = mock(ReadingCompletionHistoryJpaEntity.class);
        when(entity.getCompletedAt()).thenReturn(completedAt);
        return entity;
    }

    private DepartmentMemberJpaEntity mockDepartmentMember(UUID memberUuid, String name) {
        MemberJpaEntity member = mock(MemberJpaEntity.class);
        lenient().when(member.getId()).thenReturn(memberUuid);
        lenient().when(member.getName()).thenReturn(name);

        DepartmentMemberJpaEntity dm = mock(DepartmentMemberJpaEntity.class);
        when(dm.getMember()).thenReturn(member);
        return dm;
    }

    @Nested
    @DisplayName("isReadingEnabled - 활성화 여부")
    class IsReadingEnabledTests {

        @Test
        @DisplayName("활성 플랜이 있으면 true를 반환한다")
        void shouldReturnTrueWhenActivePlanExists() {
            DepartmentReadingPlanJpaEntity mapping = mockMappingEntity(30);
            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.of(mapping));

            assertThat(service.isReadingEnabled(departmentId)).isTrue();
        }

        @Test
        @DisplayName("활성 플랜이 없으면 false를 반환한다")
        void shouldReturnFalseWhenNoActivePlan() {
            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.empty());

            assertThat(service.isReadingEnabled(departmentId)).isFalse();
        }
    }

    @Nested
    @DisplayName("getTodayReading - 오늘 분량 조회")
    class GetTodayReadingTests {

        @Test
        @DisplayName("활성 플랜이 없으면 null을 반환한다")
        void shouldReturnNullWhenNoActivePlan() {
            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.empty());

            assertThat(service.getTodayReading(departmentId)).isNull();
        }

        @Test
        @DisplayName("오늘 날짜에 해당하는 분량을 반환한다")
        void shouldReturnTodaysDayContent() {
            DepartmentReadingPlanJpaEntity mapping = mockMappingEntity(30);
            ReadingPlanDay day = ReadingPlanDay.builder()
                    .id(ReadingPlanDayId.from(UUID.randomUUID()))
                    .readingPlanId(ReadingPlanId.from(planUuid))
                    .readingDate(LocalDate.now())
                    .dayNumber(1)
                    .readingRange("창세기 1-3장")
                    .youtubeUrl("https://youtube.com/abc")
                    .build();

            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.of(mapping));
            when(readingPlanPort.findDayByPlanIdAndDate(planUuid, LocalDate.now()))
                    .thenReturn(Optional.of(day));

            ReadingPlanDay result = service.getTodayReading(departmentId);

            assertThat(result).isNotNull();
            assertThat(result.getReadingRange()).isEqualTo("창세기 1-3장");
            assertThat(result.getYoutubeUrl()).isEqualTo("https://youtube.com/abc");
        }

        @Test
        @DisplayName("플랜에 오늘 날짜 분량이 없으면 null을 반환한다")
        void shouldReturnNullWhenNoDayForToday() {
            DepartmentReadingPlanJpaEntity mapping = mockMappingEntity(30);
            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.of(mapping));
            when(readingPlanPort.findDayByPlanIdAndDate(planUuid, LocalDate.now()))
                    .thenReturn(Optional.empty());

            assertThat(service.getTodayReading(departmentId)).isNull();
        }
    }

    @Nested
    @DisplayName("getAllDays - 전체 일자 목록")
    class GetAllDaysTests {

        @Test
        @DisplayName("활성 플랜이 없으면 빈 리스트를 반환한다")
        void shouldReturnEmptyWhenNoActivePlan() {
            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.empty());

            assertThat(service.getAllDays(departmentId)).isEmpty();
        }

        @Test
        @DisplayName("활성 플랜의 전체 일자 목록을 반환한다")
        void shouldReturnAllDaysForActivePlan() {
            DepartmentReadingPlanJpaEntity mapping = mockMappingEntity(2);
            List<ReadingPlanDay> days = List.of(
                    ReadingPlanDay.builder()
                            .id(ReadingPlanDayId.from(UUID.randomUUID()))
                            .readingPlanId(ReadingPlanId.from(planUuid))
                            .readingDate(LocalDate.of(2026, 1, 1))
                            .dayNumber(1)
                            .readingRange("창세기 1장")
                            .build(),
                    ReadingPlanDay.builder()
                            .id(ReadingPlanDayId.from(UUID.randomUUID()))
                            .readingPlanId(ReadingPlanId.from(planUuid))
                            .readingDate(LocalDate.of(2026, 1, 2))
                            .dayNumber(2)
                            .readingRange("창세기 2장")
                            .build()
            );

            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.of(mapping));
            when(readingPlanPort.findDaysByPlanId(planUuid)).thenReturn(days);

            List<ReadingPlanDay> result = service.getAllDays(departmentId);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getReadingRange()).isEqualTo("창세기 1장");
        }
    }

    @Nested
    @DisplayName("getMyProgress - 내 진도 조회")
    class GetMyProgressTests {

        @Test
        @DisplayName("활성 플랜 없으면 0으로 채워진 기본값을 반환한다")
        void shouldReturnZeroProgressWhenNoActivePlan() {
            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.empty());

            ReadingPlanQueryUseCase.MyReadingProgress result = service.getMyProgress(departmentId, memberId);

            assertThat(result.completedCount()).isZero();
            assertThat(result.totalDays()).isZero();
            assertThat(result.progressPercent()).isZero();
            assertThat(result.streak()).isZero();
            assertThat(result.completedDates()).isEmpty();
        }

        @Test
        @DisplayName("완독 수와 진도율을 올바르게 계산한다")
        void shouldCalculateProgressCorrectly() {
            // 30일 플랜 중 3일 완독 → 10%
            DepartmentReadingPlanJpaEntity mapping = mockMappingEntity(30);
            ReadingCompletionHistoryJpaEntity c1 = mockCompletion(LocalDateTime.now().minusDays(2));
            ReadingCompletionHistoryJpaEntity c2 = mockCompletion(LocalDateTime.now().minusDays(1));
            ReadingCompletionHistoryJpaEntity c3 = mockCompletion(LocalDateTime.now());

            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.of(mapping));
            when(readingCompletionHistoryJpaRepository
                    .findByDepartmentReadingPlanIdAndMemberId(deptPlanUuid, memberId.getValue()))
                    .thenReturn(List.of(c1, c2, c3));

            ReadingPlanQueryUseCase.MyReadingProgress result = service.getMyProgress(departmentId, memberId);

            assertThat(result.completedCount()).isEqualTo(3);
            assertThat(result.totalDays()).isEqualTo(30);
            assertThat(result.progressPercent()).isEqualTo(10);
            assertThat(result.completedDates()).hasSize(3);
        }

        @Test
        @DisplayName("연속 완독 스트릭을 올바르게 계산한다")
        void shouldCalculateStreakCorrectly() {
            LocalDate today = LocalDate.now();
            DepartmentReadingPlanJpaEntity mapping = mockMappingEntity(30);
            ReadingCompletionHistoryJpaEntity c1 = mockCompletion(today.atStartOfDay());
            ReadingCompletionHistoryJpaEntity c2 = mockCompletion(today.minusDays(1).atStartOfDay());
            ReadingCompletionHistoryJpaEntity c3 = mockCompletion(today.minusDays(2).atStartOfDay());

            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.of(mapping));
            when(readingCompletionHistoryJpaRepository
                    .findByDepartmentReadingPlanIdAndMemberId(deptPlanUuid, memberId.getValue()))
                    .thenReturn(List.of(c1, c2, c3));

            ReadingPlanQueryUseCase.MyReadingProgress result = service.getMyProgress(departmentId, memberId);

            assertThat(result.streak()).isEqualTo(3);
        }

        @Test
        @DisplayName("오늘 완독 기록이 없으면 streak이 0이다")
        void shouldReturnZeroStreakWhenTodayNotCompleted() {
            LocalDate today = LocalDate.now();
            DepartmentReadingPlanJpaEntity mapping = mockMappingEntity(30);
            ReadingCompletionHistoryJpaEntity c1 = mockCompletion(today.minusDays(1).atStartOfDay());
            ReadingCompletionHistoryJpaEntity c2 = mockCompletion(today.minusDays(2).atStartOfDay());

            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.of(mapping));
            when(readingCompletionHistoryJpaRepository
                    .findByDepartmentReadingPlanIdAndMemberId(deptPlanUuid, memberId.getValue()))
                    .thenReturn(List.of(c1, c2));

            ReadingPlanQueryUseCase.MyReadingProgress result = service.getMyProgress(departmentId, memberId);

            assertThat(result.streak()).isZero();
        }

        @Test
        @DisplayName("완독 날짜 목록이 오름차순으로 정렬된다")
        void shouldReturnCompletedDatesSorted() {
            LocalDate today = LocalDate.now();
            DepartmentReadingPlanJpaEntity mapping = mockMappingEntity(10);
            ReadingCompletionHistoryJpaEntity c1 = mockCompletion(today.atStartOfDay());
            ReadingCompletionHistoryJpaEntity c2 = mockCompletion(today.minusDays(3).atStartOfDay());
            ReadingCompletionHistoryJpaEntity c3 = mockCompletion(today.minusDays(1).atStartOfDay());

            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.of(mapping));
            when(readingCompletionHistoryJpaRepository
                    .findByDepartmentReadingPlanIdAndMemberId(deptPlanUuid, memberId.getValue()))
                    .thenReturn(List.of(c1, c2, c3));

            ReadingPlanQueryUseCase.MyReadingProgress result = service.getMyProgress(departmentId, memberId);

            assertThat(result.completedDates()).isSorted();
            assertThat(result.completedDates().get(0)).isEqualTo(today.minusDays(3));
        }
    }

    @Nested
    @DisplayName("getDepartmentProgress - 부서 전체 진도 조회")
    class GetDepartmentProgressTests {

        @Test
        @DisplayName("활성 플랜이 없으면 빈 리스트를 반환한다")
        void shouldReturnEmptyWhenNoActivePlan() {
            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.empty());

            assertThat(service.getDepartmentProgress(departmentId)).isEmpty();
        }

        @Test
        @DisplayName("ACTIVE 부서 멤버별 진도율을 반환한다")
        void shouldReturnProgressForActiveMembers() {
            UUID memberAId = UUID.randomUUID();
            UUID memberBId = UUID.randomUUID();

            DepartmentReadingPlanJpaEntity mapping = mockMappingEntity(10);
            DepartmentMemberJpaEntity dmA = mockDepartmentMember(memberAId, "김철수");
            DepartmentMemberJpaEntity dmB = mockDepartmentMember(memberBId, "이영희");

            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.of(mapping));
            when(departmentMemberJpaRepository.findByDepartmentIdAndStatus(deptUuid, DepartmentMemberStatus.ACTIVE))
                    .thenReturn(List.of(dmA, dmB));
            when(readingCompletionHistoryJpaRepository.countByDepartmentReadingPlanIdAndMemberId(deptPlanUuid, memberAId))
                    .thenReturn(7L);
            when(readingCompletionHistoryJpaRepository.countByDepartmentReadingPlanIdAndMemberId(deptPlanUuid, memberBId))
                    .thenReturn(5L);

            List<ReadingPlanQueryUseCase.MemberReadingProgress> result = service.getDepartmentProgress(departmentId);

            assertThat(result).hasSize(2);

            ReadingPlanQueryUseCase.MemberReadingProgress progressA = result.stream()
                    .filter(p -> p.memberName().equals("김철수")).findFirst().orElseThrow();
            assertThat(progressA.completedCount()).isEqualTo(7);
            assertThat(progressA.totalDays()).isEqualTo(10);
            assertThat(progressA.progressPercent()).isEqualTo(70);

            ReadingPlanQueryUseCase.MemberReadingProgress progressB = result.stream()
                    .filter(p -> p.memberName().equals("이영희")).findFirst().orElseThrow();
            assertThat(progressB.progressPercent()).isEqualTo(50);
        }

        @Test
        @DisplayName("부서 멤버가 없으면 빈 리스트를 반환한다")
        void shouldReturnEmptyWhenNoDepartmentMembers() {
            DepartmentReadingPlanJpaEntity mapping = mockMappingEntity(10);
            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.of(mapping));
            when(departmentMemberJpaRepository.findByDepartmentIdAndStatus(deptUuid, DepartmentMemberStatus.ACTIVE))
                    .thenReturn(List.of());

            assertThat(service.getDepartmentProgress(departmentId)).isEmpty();
        }
    }
}

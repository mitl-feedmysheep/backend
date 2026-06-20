package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.DepartmentReadingPlanJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.DepartmentReadingPlanJpaRepository;
import mitl.IntoTheHeaven.application.port.out.ReadingCompletionHistoryPort;
import mitl.IntoTheHeaven.application.port.out.ReadingPlanPort;
import mitl.IntoTheHeaven.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadingCompletionCommandServiceTest {

    @Mock
    private ReadingCompletionHistoryPort readingCompletionHistoryPort;

    @Mock
    private ReadingPlanPort readingPlanPort;

    @Mock
    private DepartmentReadingPlanJpaRepository departmentReadingPlanJpaRepository;

    @InjectMocks
    private ReadingCompletionCommandService service;

    private UUID deptUuid;
    private UUID deptPlanUuid;
    private UUID dayUuid;
    private UUID memberUuid;
    private DepartmentId departmentId;
    private ReadingPlanDayId dayId;
    private MemberId memberId;

    @BeforeEach
    void setUp() {
        deptUuid = UUID.randomUUID();
        deptPlanUuid = UUID.randomUUID();
        dayUuid = UUID.randomUUID();
        memberUuid = UUID.randomUUID();
        departmentId = DepartmentId.from(deptUuid);
        dayId = ReadingPlanDayId.from(dayUuid);
        memberId = MemberId.from(memberUuid);
    }

    private DepartmentReadingPlanJpaEntity mockMapping() {
        DepartmentReadingPlanJpaEntity mapping = mock(DepartmentReadingPlanJpaEntity.class);
        when(mapping.getId()).thenReturn(deptPlanUuid);
        return mapping;
    }

    private ReadingPlanDay buildDay() {
        return ReadingPlanDay.builder()
                .id(dayId)
                .readingPlanId(ReadingPlanId.from(UUID.randomUUID()))
                .dayNumber(1)
                .readingRange("창세기 1장")
                .build();
    }

    private ReadingCompletionHistory buildHistory(boolean isCompleted) {
        return ReadingCompletionHistory.builder()
                .id(ReadingCompletionHistoryId.from(UUID.randomUUID()))
                .departmentReadingPlanId(DepartmentReadingPlanId.from(deptPlanUuid))
                .readingPlanDayId(dayId)
                .memberId(memberId)
                .completedAt(java.time.LocalDateTime.now())
                .isCompleted(isCompleted)
                .build();
    }

    @Nested
    @DisplayName("markComplete - 완독 체크")
    class MarkCompleteTests {

        @Test
        @DisplayName("활성 플랜이 없으면 RuntimeException이 발생한다")
        void shouldThrowWhenNoActivePlan() {
            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.markComplete(departmentId, dayId, memberId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("No active reading plan");
        }

        @Test
        @DisplayName("이미 is_completed=true 이면 중복 저장하지 않는다")
        void shouldSkipWhenAlreadyCompleted() {
            var mapping = mockMapping();
            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.of(mapping));
            when(readingCompletionHistoryPort.findByDeptPlanIdAndDayIdAndMemberId(deptPlanUuid, dayUuid, memberUuid))
                    .thenReturn(Optional.of(buildHistory(true)));

            service.markComplete(departmentId, dayId, memberId);

            verify(readingCompletionHistoryPort, never()).save(any());
            verify(readingCompletionHistoryPort, never()).setIsCompleted(any(), any(), any(), anyBoolean());
        }

        @Test
        @DisplayName("is_completed=false 상태이면 setIsCompleted(true)로 재활성화한다")
        void shouldReactivateWhenCancelled() {
            var mapping = mockMapping();
            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.of(mapping));
            when(readingCompletionHistoryPort.findByDeptPlanIdAndDayIdAndMemberId(deptPlanUuid, dayUuid, memberUuid))
                    .thenReturn(Optional.of(buildHistory(false)));

            service.markComplete(departmentId, dayId, memberId);

            verify(readingCompletionHistoryPort).setIsCompleted(deptPlanUuid, dayUuid, memberUuid, true);
            verify(readingCompletionHistoryPort, never()).save(any());
        }

        @Test
        @DisplayName("존재하지 않는 dayId면 RuntimeException이 발생한다")
        void shouldThrowWhenDayNotFound() {
            var mapping = mockMapping();
            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.of(mapping));
            when(readingCompletionHistoryPort.findByDeptPlanIdAndDayIdAndMemberId(any(), any(), any()))
                    .thenReturn(Optional.empty());
            when(readingPlanPort.findDayById(dayUuid)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.markComplete(departmentId, dayId, memberId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Reading plan day not found");
        }

        @Test
        @DisplayName("최초 완독 시 is_completed=true, completed_at=실제 시간으로 저장된다")
        void shouldSaveWithIsCompletedTrueAndRealTime() {
            var mapping = mockMapping();
            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.of(mapping));
            when(readingCompletionHistoryPort.findByDeptPlanIdAndDayIdAndMemberId(any(), any(), any()))
                    .thenReturn(Optional.empty());
            when(readingPlanPort.findDayById(dayUuid)).thenReturn(Optional.of(buildDay()));
            when(readingCompletionHistoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

            var before = java.time.LocalDateTime.now().minusSeconds(1);
            service.markComplete(departmentId, dayId, memberId);
            var after = java.time.LocalDateTime.now().plusSeconds(1);

            ArgumentCaptor<ReadingCompletionHistory> captor = ArgumentCaptor.forClass(ReadingCompletionHistory.class);
            verify(readingCompletionHistoryPort).save(captor.capture());

            ReadingCompletionHistory saved = captor.getValue();
            assertThat(saved.isCompleted()).isTrue();
            assertThat(saved.getCompletedAt()).isBetween(before, after);
            assertThat(saved.getDepartmentReadingPlanId().getValue()).isEqualTo(deptPlanUuid);
        }
    }

    @Nested
    @DisplayName("unmarkComplete - 완독 취소")
    class UnmarkCompleteTests {

        @Test
        @DisplayName("활성 플랜이 있으면 setIsCompleted(false)를 호출한다")
        void shouldSetIsCompletedFalseWhenActivePlanExists() {
            var mapping = mockMapping();
            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.of(mapping));

            service.unmarkComplete(departmentId, dayId, memberId);

            verify(readingCompletionHistoryPort).setIsCompleted(deptPlanUuid, dayUuid, memberUuid, false);
        }

        @Test
        @DisplayName("활성 플랜이 없으면 아무것도 하지 않는다")
        void shouldDoNothingWhenNoActivePlan() {
            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.empty());

            service.unmarkComplete(departmentId, dayId, memberId);

            verify(readingCompletionHistoryPort, never()).setIsCompleted(any(), any(), any(), anyBoolean());
        }
    }
}

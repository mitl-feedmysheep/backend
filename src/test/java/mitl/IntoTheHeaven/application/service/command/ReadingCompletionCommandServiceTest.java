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

    private DepartmentReadingPlanJpaEntity mockMappingEntity() {
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
        @DisplayName("이미 완독 기록이 있으면 중복 저장하지 않는다")
        void shouldSkipWhenAlreadyCompleted() {
            DepartmentReadingPlanJpaEntity mapping = mockMappingEntity();
            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.of(mapping));
            when(readingCompletionHistoryPort.existsByDeptPlanIdAndDayIdAndMemberId(deptPlanUuid, dayUuid, memberUuid))
                    .thenReturn(true);

            service.markComplete(departmentId, dayId, memberId);

            verify(readingCompletionHistoryPort, never()).save(any());
        }

        @Test
        @DisplayName("존재하지 않는 dayId면 RuntimeException이 발생한다")
        void shouldThrowWhenDayNotFound() {
            DepartmentReadingPlanJpaEntity mapping = mockMappingEntity();
            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.of(mapping));
            when(readingCompletionHistoryPort.existsByDeptPlanIdAndDayIdAndMemberId(any(), any(), any()))
                    .thenReturn(false);
            when(readingPlanPort.findDayById(dayUuid)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.markComplete(departmentId, dayId, memberId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Reading plan day not found");
        }

        @Test
        @DisplayName("정상 완독 체크 시 완독 이력이 저장된다")
        void shouldSaveCompletionHistory() {
            DepartmentReadingPlanJpaEntity mapping = mockMappingEntity();
            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.of(mapping));
            when(readingCompletionHistoryPort.existsByDeptPlanIdAndDayIdAndMemberId(any(), any(), any()))
                    .thenReturn(false);
            when(readingPlanPort.findDayById(dayUuid)).thenReturn(Optional.of(buildDay()));
            when(readingCompletionHistoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.markComplete(departmentId, dayId, memberId);

            ArgumentCaptor<ReadingCompletionHistory> captor = ArgumentCaptor.forClass(ReadingCompletionHistory.class);
            verify(readingCompletionHistoryPort).save(captor.capture());

            ReadingCompletionHistory saved = captor.getValue();
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getDepartmentReadingPlanId().getValue()).isEqualTo(deptPlanUuid);
            assertThat(saved.getReadingPlanDayId()).isEqualTo(dayId);
            assertThat(saved.getMemberId()).isEqualTo(memberId);
            assertThat(saved.getCompletedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("unmarkComplete - 완독 취소")
    class UnmarkCompleteTests {

        @Test
        @DisplayName("활성 플랜이 있으면 완독 이력을 삭제한다")
        void shouldDeleteWhenActivePlanExists() {
            DepartmentReadingPlanJpaEntity mapping = mockMappingEntity();
            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.of(mapping));

            service.unmarkComplete(departmentId, dayId, memberId);

            verify(readingCompletionHistoryPort)
                    .deleteByDeptPlanIdAndDayIdAndMemberId(deptPlanUuid, dayUuid, memberUuid);
        }

        @Test
        @DisplayName("활성 플랜이 없으면 삭제를 시도하지 않는다")
        void shouldDoNothingWhenNoActivePlan() {
            when(departmentReadingPlanJpaRepository.findActiveByDepartmentIdAndDate(eq(deptUuid), any()))
                    .thenReturn(Optional.empty());

            service.unmarkComplete(departmentId, dayId, memberId);

            verify(readingCompletionHistoryPort, never())
                    .deleteByDeptPlanIdAndDayIdAndMemberId(any(), any(), any());
        }
    }
}

package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.in.command.ReadingPlanCommandUseCase;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadingPlanCommandServiceTest {

    @Mock
    private ReadingPlanPort readingPlanPort;

    @InjectMocks
    private ReadingPlanCommandService service;

    private UUID deptUuid;
    private UUID planUuid;
    private UUID churchUuid;
    private DepartmentId departmentId;

    @BeforeEach
    void setUp() {
        deptUuid = UUID.randomUUID();
        planUuid = UUID.randomUUID();
        churchUuid = UUID.randomUUID();
        departmentId = DepartmentId.from(deptUuid);
    }

    @Nested
    @DisplayName("createPlan - 플랜 생성")
    class CreatePlanTests {

        @Test
        @DisplayName("올바른 필드로 플랜이 생성되고 저장된다")
        void shouldCreateAndSavePlanWithCorrectFields() {
            when(readingPlanPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.createPlan(churchUuid, "2026 창세기 통독", 63);

            ArgumentCaptor<ReadingPlan> captor = ArgumentCaptor.forClass(ReadingPlan.class);
            verify(readingPlanPort).save(captor.capture());
            ReadingPlan saved = captor.getValue();

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getChurchId()).isEqualTo(churchUuid);
            assertThat(saved.getTitle()).isEqualTo("2026 창세기 통독");
            assertThat(saved.getReadingDays()).isEqualTo(63);
        }

        @Test
        @DisplayName("각 호출마다 서로 다른 UUID가 할당된다")
        void shouldAssignUniqueIdPerCall() {
            when(readingPlanPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.createPlan(churchUuid, "플랜A", 63);
            service.createPlan(churchUuid, "플랜B", 31);

            ArgumentCaptor<ReadingPlan> captor = ArgumentCaptor.forClass(ReadingPlan.class);
            verify(readingPlanPort, times(2)).save(captor.capture());

            List<ReadingPlan> saved = captor.getAllValues();
            assertThat(saved.get(0).getId()).isNotEqualTo(saved.get(1).getId());
        }
    }

    @Nested
    @DisplayName("createDaysBatch - 일자 일괄 등록")
    class CreateDaysBatchTests {

        @Test
        @DisplayName("입력 수만큼 ReadingPlanDay가 저장된다")
        void shouldSaveCorrectNumberOfDays() {
            when(readingPlanPort.saveDays(any())).thenAnswer(inv -> inv.getArgument(0));

            List<ReadingPlanCommandUseCase.DayInput> inputs = List.of(
                    new ReadingPlanCommandUseCase.DayInput(1, "창세기 1-3장", null, null, null),
                    new ReadingPlanCommandUseCase.DayInput(2, "창세기 4-6장", "https://audio.link", "https://video.link", "요약")
            );

            service.createDaysBatch(planUuid, inputs);

            ArgumentCaptor<List<ReadingPlanDay>> captor = ArgumentCaptor.forClass(List.class);
            verify(readingPlanPort).saveDays(captor.capture());
            assertThat(captor.getValue()).hasSize(2);
        }

        @Test
        @DisplayName("각 day의 readingPlanId가 전달된 planId와 일치한다")
        void shouldSetCorrectReadingPlanId() {
            when(readingPlanPort.saveDays(any())).thenAnswer(inv -> inv.getArgument(0));

            service.createDaysBatch(planUuid, List.of(
                    new ReadingPlanCommandUseCase.DayInput(1, "창세기 1장", "https://audio.link", "https://video.link", "설명")
            ));

            ArgumentCaptor<List<ReadingPlanDay>> captor = ArgumentCaptor.forClass(List.class);
            verify(readingPlanPort).saveDays(captor.capture());

            ReadingPlanDay day = captor.getValue().get(0);
            assertThat(day.getReadingPlanId().getValue()).isEqualTo(planUuid);
            assertThat(day.getReadingRange()).isEqualTo("창세기 1장");
            assertThat(day.getDayNumber()).isEqualTo(1);
            assertThat(day.getAudioUrl()).isEqualTo("https://audio.link");
            assertThat(day.getVideoUrl()).isEqualTo("https://video.link");
            assertThat(day.getDescription()).isEqualTo("설명");
        }
    }

    @Nested
    @DisplayName("activatePlanForDepartment - 플랜 활성화")
    class ActivatePlanTests {

        @Test
        @DisplayName("기존 활성 매핑이 없으면 바로 새 매핑을 생성한다")
        void shouldCreateNewMappingWhenNoExistingMapping() {
            when(readingPlanPort.findActiveMappingByDepartmentId(deptUuid)).thenReturn(Optional.empty());
            when(readingPlanPort.saveMapping(any())).thenAnswer(inv -> inv.getArgument(0));

            service.activatePlanForDepartment(departmentId, planUuid,
                    LocalDate.of(2026, 1, 5), LocalDate.of(2026, 3, 28));

            ArgumentCaptor<DepartmentReadingPlan> captor = ArgumentCaptor.forClass(DepartmentReadingPlan.class);
            verify(readingPlanPort, times(1)).saveMapping(captor.capture());

            DepartmentReadingPlan created = captor.getValue();
            assertThat(created.getDepartmentId()).isEqualTo(departmentId);
            assertThat(created.getReadingPlanId().getValue()).isEqualTo(planUuid);
            assertThat(created.getStartDate()).isEqualTo(LocalDate.of(2026, 1, 5));
            assertThat(created.getEndDate()).isEqualTo(LocalDate.of(2026, 3, 28));
            assertThat(created.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("기존 활성 매핑이 있으면 soft delete 후 새 매핑을 생성한다")
        void shouldSoftDeleteExistingMappingBeforeCreatingNew() {
            DepartmentReadingPlan existing = DepartmentReadingPlan.builder()
                    .id(DepartmentReadingPlanId.from(UUID.randomUUID()))
                    .departmentId(departmentId)
                    .readingPlanId(ReadingPlanId.from(UUID.randomUUID()))
                    .startDate(LocalDate.of(2025, 1, 6))
                    .endDate(LocalDate.of(2025, 3, 29))
                    .build();

            when(readingPlanPort.findActiveMappingByDepartmentId(deptUuid)).thenReturn(Optional.of(existing));
            when(readingPlanPort.saveMapping(any())).thenAnswer(inv -> inv.getArgument(0));

            service.activatePlanForDepartment(departmentId, planUuid,
                    LocalDate.of(2026, 1, 5), LocalDate.of(2026, 3, 28));

            ArgumentCaptor<DepartmentReadingPlan> captor = ArgumentCaptor.forClass(DepartmentReadingPlan.class);
            verify(readingPlanPort, times(2)).saveMapping(captor.capture());

            DepartmentReadingPlan deleted = captor.getAllValues().get(0);
            DepartmentReadingPlan created = captor.getAllValues().get(1);

            assertThat(deleted.getId()).isEqualTo(existing.getId());
            assertThat(deleted.getDeletedAt()).isNotNull();
            assertThat(created.getReadingPlanId().getValue()).isEqualTo(planUuid);
            assertThat(created.getDeletedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("deactivatePlanForDepartment - 플랜 비활성화")
    class DeactivatePlanTests {

        @Test
        @DisplayName("활성 매핑이 있으면 soft delete 한다")
        void shouldSoftDeleteExistingMapping() {
            DepartmentReadingPlan existing = DepartmentReadingPlan.builder()
                    .id(DepartmentReadingPlanId.from(UUID.randomUUID()))
                    .departmentId(departmentId)
                    .readingPlanId(ReadingPlanId.from(UUID.randomUUID()))
                    .startDate(LocalDate.of(2026, 1, 6))
                    .endDate(LocalDate.of(2026, 3, 29))
                    .build();

            when(readingPlanPort.findActiveMappingByDepartmentId(deptUuid)).thenReturn(Optional.of(existing));
            when(readingPlanPort.saveMapping(any())).thenAnswer(inv -> inv.getArgument(0));

            service.deactivatePlanForDepartment(departmentId);

            ArgumentCaptor<DepartmentReadingPlan> captor = ArgumentCaptor.forClass(DepartmentReadingPlan.class);
            verify(readingPlanPort).saveMapping(captor.capture());
            assertThat(captor.getValue().getId()).isEqualTo(existing.getId());
            assertThat(captor.getValue().getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("활성 매핑이 없으면 아무것도 하지 않는다")
        void shouldDoNothingWhenNoActiveMapping() {
            when(readingPlanPort.findActiveMappingByDepartmentId(deptUuid)).thenReturn(Optional.empty());

            service.deactivatePlanForDepartment(departmentId);

            verify(readingPlanPort, never()).saveMapping(any());
        }
    }
}

package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.AddVisitMembersCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.CreateVisitCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateVisitCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateVisitMemberCommand;
import mitl.IntoTheHeaven.application.port.out.ChurchPort;
import mitl.IntoTheHeaven.application.port.out.VisitPort;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitCommandServiceTest {

    @Mock
    private VisitPort visitPort;

    @Mock
    private ChurchPort churchPort;

    @InjectMocks
    private VisitCommandService visitCommandService;

    private ChurchId churchId;
    private ChurchId otherChurchId;
    private VisitId visitId;
    private MemberId pastorMemberId;
    private ChurchMemberId pastorChurchMemberId;
    private Visit existingVisit;

    @BeforeEach
    void setUp() {
        churchId = ChurchId.from(UUID.randomUUID());
        otherChurchId = ChurchId.from(UUID.randomUUID());
        visitId = VisitId.from(UUID.randomUUID());
        pastorMemberId = MemberId.from(UUID.randomUUID());
        pastorChurchMemberId = ChurchMemberId.from(UUID.randomUUID());

        existingVisit = Visit.builder()
                .id(visitId)
                .churchId(churchId)
                .pastorMemberId(pastorChurchMemberId)
                .date(LocalDate.of(2025, 6, 1))
                .startedAt(LocalDateTime.of(2025, 6, 1, 14, 0))
                .endedAt(LocalDateTime.of(2025, 6, 1, 16, 0))
                .place("홍길동 자택")
                .expense(50000)
                .notes("기존 메모")
                .visitMembers(new ArrayList<>())
                .build();
    }

    @Nested
    @DisplayName("createVisit - 심방 생성")
    class CreateVisitTests {

        @Test
        @DisplayName("심방이 정상적으로 생성된다")
        void shouldCreateVisitSuccessfully() {
            ChurchMember pastorChurchMember = ChurchMember.builder()
                    .id(pastorChurchMemberId)
                    .churchId(churchId)
                    .memberId(pastorMemberId)
                    .build();

            CreateVisitCommand command = CreateVisitCommand.builder()
                    .churchId(churchId)
                    .pastorMemberId(pastorMemberId)
                    .date(LocalDate.of(2025, 7, 1))
                    .startedAt(LocalDateTime.of(2025, 7, 1, 10, 0))
                    .endedAt(LocalDateTime.of(2025, 7, 1, 12, 0))
                    .place("교회")
                    .expense(30000)
                    .notes("심방 메모")
                    .build();

            when(churchPort.findChurchMemberByMemberIdAndChurchId(pastorMemberId, churchId))
                    .thenReturn(pastorChurchMember);
            when(visitPort.save(any(Visit.class))).thenAnswer(inv -> inv.getArgument(0));

            Visit result = visitCommandService.createVisit(command);

            ArgumentCaptor<Visit> captor = ArgumentCaptor.forClass(Visit.class);
            verify(visitPort).save(captor.capture());
            Visit saved = captor.getValue();

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getChurchId()).isEqualTo(churchId);
            assertThat(saved.getPastorMemberId()).isEqualTo(pastorChurchMemberId);
            assertThat(saved.getDate()).isEqualTo(LocalDate.of(2025, 7, 1));
            assertThat(saved.getPlace()).isEqualTo("교회");
            assertThat(saved.getExpense()).isEqualTo(30000);
        }
    }

    @Nested
    @DisplayName("updateVisit - 심방 업데이트")
    class UpdateVisitTests {

        @Test
        @DisplayName("심방 기본 정보가 업데이트된다")
        void shouldUpdateVisitBasicInfo() {
            UpdateVisitCommand command = UpdateVisitCommand.builder()
                    .date(LocalDate.of(2025, 8, 1))
                    .startedAt(LocalDateTime.of(2025, 8, 1, 10, 0))
                    .endedAt(LocalDateTime.of(2025, 8, 1, 12, 0))
                    .place("새 장소")
                    .expense(100000)
                    .notes("새 메모")
                    .build();

            when(visitPort.findDetailById(visitId)).thenReturn(Optional.of(existingVisit));
            when(visitPort.save(any(Visit.class))).thenAnswer(inv -> inv.getArgument(0));

            Visit result = visitCommandService.updateVisit(visitId, command, churchId);

            ArgumentCaptor<Visit> captor = ArgumentCaptor.forClass(Visit.class);
            verify(visitPort).save(captor.capture());
            Visit saved = captor.getValue();

            assertThat(saved.getDate()).isEqualTo(LocalDate.of(2025, 8, 1));
            assertThat(saved.getPlace()).isEqualTo("새 장소");
            assertThat(saved.getExpense()).isEqualTo(100000);
            assertThat(saved.getNotes()).isEqualTo("새 메모");
        }

        @Test
        @DisplayName("다른 교회의 심방은 수정할 수 없다")
        void shouldThrowWhenChurchDoesNotMatch() {
            UpdateVisitCommand command = UpdateVisitCommand.builder()
                    .date(LocalDate.of(2025, 8, 1))
                    .build();

            when(visitPort.findDetailById(visitId)).thenReturn(Optional.of(existingVisit));

            assertThatThrownBy(() -> visitCommandService.updateVisit(visitId, command, otherChurchId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Access denied");
        }

        @Test
        @DisplayName("심방이 존재하지 않으면 IllegalArgumentException이 발생한다")
        void shouldThrowWhenVisitNotFound() {
            UpdateVisitCommand command = UpdateVisitCommand.builder().build();

            when(visitPort.findDetailById(visitId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> visitCommandService.updateVisit(visitId, command, churchId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Visit not found");
        }
    }

    @Nested
    @DisplayName("deleteVisit - 심방 삭제")
    class DeleteVisitTests {

        @Test
        @DisplayName("심방이 soft delete된다")
        void shouldSoftDeleteVisit() {
            when(visitPort.findDetailById(visitId)).thenReturn(Optional.of(existingVisit));

            visitCommandService.deleteVisit(visitId, churchId);

            verify(visitPort).delete(existingVisit);
        }

        @Test
        @DisplayName("다른 교회의 심방은 삭제할 수 없다")
        void shouldThrowWhenChurchDoesNotMatch() {
            when(visitPort.findDetailById(visitId)).thenReturn(Optional.of(existingVisit));

            assertThatThrownBy(() -> visitCommandService.deleteVisit(visitId, otherChurchId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Access denied");

            verify(visitPort, never()).delete(any());
        }

        @Test
        @DisplayName("심방이 존재하지 않으면 IllegalArgumentException이 발생한다")
        void shouldThrowWhenVisitNotFound() {
            when(visitPort.findDetailById(visitId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> visitCommandService.deleteVisit(visitId, churchId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Visit not found");
        }
    }

    @Nested
    @DisplayName("addMembersToVisit - 심방 멤버 추가")
    class AddMembersToVisitTests {

        @Test
        @DisplayName("교회 멤버를 심방 멤버로 추가한다")
        void shouldAddMembersSuccessfully() {
            MemberId newMemberId = MemberId.from(UUID.randomUUID());
            ChurchMemberId newChurchMemberId = ChurchMemberId.from(UUID.randomUUID());
            ChurchMember newChurchMember = ChurchMember.builder()
                    .id(newChurchMemberId)
                    .churchId(churchId)
                    .memberId(newMemberId)
                    .build();

            AddVisitMembersCommand command = AddVisitMembersCommand.builder()
                    .memberIds(List.of(newMemberId))
                    .build();

            when(visitPort.findDetailById(visitId)).thenReturn(Optional.of(existingVisit));
            when(churchPort.findChurchMemberByMemberIdAndChurchId(newMemberId, churchId))
                    .thenReturn(newChurchMember);
            when(visitPort.save(any(Visit.class))).thenAnswer(inv -> inv.getArgument(0));

            Visit result = visitCommandService.addMembersToVisit(visitId, command, churchId);

            ArgumentCaptor<Visit> captor = ArgumentCaptor.forClass(Visit.class);
            verify(visitPort).save(captor.capture());
            Visit saved = captor.getValue();

            assertThat(saved.getVisitMembers()).hasSize(1);
            assertThat(saved.getVisitMembers().get(0).getChurchMemberId()).isEqualTo(newChurchMemberId);
            assertThat(saved.getVisitMembers().get(0).getStory()).isNull();
            assertThat(saved.getVisitMembers().get(0).getPrayers()).isEmpty();
        }

        @Test
        @DisplayName("교회 멤버가 아닌 사람을 추가하면 IllegalArgumentException이 발생한다")
        void shouldThrowWhenMemberIsNotChurchMember() {
            MemberId nonChurchMemberId = MemberId.from(UUID.randomUUID());

            AddVisitMembersCommand command = AddVisitMembersCommand.builder()
                    .memberIds(List.of(nonChurchMemberId))
                    .build();

            when(visitPort.findDetailById(visitId)).thenReturn(Optional.of(existingVisit));
            when(churchPort.findChurchMemberByMemberIdAndChurchId(nonChurchMemberId, churchId))
                    .thenReturn(null);

            assertThatThrownBy(() -> visitCommandService.addMembersToVisit(visitId, command, churchId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Church member not found");
        }

        @Test
        @DisplayName("이미 추가된 멤버를 중복 추가하면 IllegalArgumentException이 발생한다")
        void shouldThrowWhenMemberAlreadyAdded() {
            ChurchMemberId existingChurchMemberId = ChurchMemberId.from(UUID.randomUUID());
            MemberId duplicateMemberId = MemberId.from(UUID.randomUUID());

            VisitMember existingVm = VisitMember.builder()
                    .id(VisitMemberId.from(UUID.randomUUID()))
                    .visitId(visitId)
                    .churchMemberId(existingChurchMemberId)
                    .build();

            Visit visitWithMembers = existingVisit.toBuilder()
                    .visitMembers(new ArrayList<>(List.of(existingVm)))
                    .build();

            ChurchMember duplicateChurchMember = ChurchMember.builder()
                    .id(existingChurchMemberId)
                    .churchId(churchId)
                    .memberId(duplicateMemberId)
                    .build();

            AddVisitMembersCommand command = AddVisitMembersCommand.builder()
                    .memberIds(List.of(duplicateMemberId))
                    .build();

            when(visitPort.findDetailById(visitId)).thenReturn(Optional.of(visitWithMembers));
            when(churchPort.findChurchMemberByMemberIdAndChurchId(duplicateMemberId, churchId))
                    .thenReturn(duplicateChurchMember);

            assertThatThrownBy(() -> visitCommandService.addMembersToVisit(visitId, command, churchId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already added");
        }

        @Test
        @DisplayName("다른 교회의 심방에 멤버를 추가할 수 없다")
        void shouldThrowWhenChurchDoesNotMatch() {
            AddVisitMembersCommand command = AddVisitMembersCommand.builder()
                    .memberIds(List.of(MemberId.from(UUID.randomUUID())))
                    .build();

            when(visitPort.findDetailById(visitId)).thenReturn(Optional.of(existingVisit));

            assertThatThrownBy(() -> visitCommandService.addMembersToVisit(visitId, command, otherChurchId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Access denied");
        }
    }

    @Nested
    @DisplayName("removeMemberFromVisit - 심방 멤버 제거")
    class RemoveMemberFromVisitTests {

        @Test
        @DisplayName("심방 멤버를 soft delete한다")
        void shouldSoftDeleteVisitMember() {
            VisitMemberId vmId = VisitMemberId.from(UUID.randomUUID());
            VisitMember vm = VisitMember.builder()
                    .id(vmId)
                    .visitId(visitId)
                    .churchMemberId(ChurchMemberId.from(UUID.randomUUID()))
                    .build();

            Visit visitWithMember = existingVisit.toBuilder()
                    .visitMembers(new ArrayList<>(List.of(vm)))
                    .build();

            when(visitPort.findDetailById(visitId)).thenReturn(Optional.of(visitWithMember));
            when(visitPort.save(any(Visit.class))).thenAnswer(inv -> inv.getArgument(0));

            visitCommandService.removeMemberFromVisit(visitId, vmId, churchId);

            ArgumentCaptor<Visit> captor = ArgumentCaptor.forClass(Visit.class);
            verify(visitPort).save(captor.capture());
            Visit saved = captor.getValue();

            VisitMember deletedVm = saved.getVisitMembers().stream()
                    .filter(v -> v.getId().equals(vmId))
                    .findFirst().orElseThrow();
            assertThat(deletedVm.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("다른 교회의 심방에서 멤버를 제거할 수 없다")
        void shouldThrowWhenChurchDoesNotMatch() {
            VisitMemberId vmId = VisitMemberId.from(UUID.randomUUID());

            when(visitPort.findDetailById(visitId)).thenReturn(Optional.of(existingVisit));

            assertThatThrownBy(() -> visitCommandService.removeMemberFromVisit(visitId, vmId, otherChurchId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Access denied");
        }
    }

    @Nested
    @DisplayName("updateVisitMember - 심방 멤버 스토리/기도 업데이트")
    class UpdateVisitMemberTests {

        private VisitMemberId vmId;
        private ChurchMemberId cmId;
        private Visit visitWithMember;
        private Prayer existingPrayer;

        @BeforeEach
        void setUpVisitWithMember() {
            vmId = VisitMemberId.from(UUID.randomUUID());
            cmId = ChurchMemberId.from(UUID.randomUUID());
            MemberId memId = MemberId.from(UUID.randomUUID());

            existingPrayer = Prayer.builder()
                    .id(PrayerId.from(UUID.randomUUID()))
                    .member(Member.builder().id(memId).name("테스트").build())
                    .memberId(memId)
                    .visitMemberId(vmId)
                    .prayerRequest("기존 기도")
                    .description("기존 설명")
                    .isAnswered(true)
                    .build();

            VisitMember vm = VisitMember.builder()
                    .id(vmId)
                    .visitId(visitId)
                    .churchMemberId(cmId)
                    .churchMember(ChurchMember.builder()
                            .id(cmId)
                            .churchId(churchId)
                            .memberId(memId)
                            .member(Member.builder().id(memId).name("테스트").build())
                            .build())
                    .story("기존 스토리")
                    .prayers(new ArrayList<>(List.of(existingPrayer)))
                    .build();

            visitWithMember = existingVisit.toBuilder()
                    .visitMembers(new ArrayList<>(List.of(vm)))
                    .build();
        }

        @Test
        @DisplayName("기존 기도제목 업데이트 시 isAnswered가 보존된다")
        void shouldPreserveIsAnsweredForExistingPrayer() {
            UUID existingPrayerUuid = existingPrayer.getId().getValue();

            UpdateVisitMemberCommand.PrayerUpdateCommand pCmd =
                    new UpdateVisitMemberCommand.PrayerUpdateCommand(
                            existingPrayerUuid, "수정된 기도", "수정된 설명");

            UpdateVisitMemberCommand command = new UpdateVisitMemberCommand(
                    visitId, vmId, "새 스토리", List.of(pCmd));

            when(visitPort.findDetailById(visitId)).thenReturn(Optional.of(visitWithMember));
            when(visitPort.save(any(Visit.class))).thenAnswer(inv -> inv.getArgument(0));

            VisitMember result = visitCommandService.updateVisitMember(command, churchId);

            Prayer updated = result.getPrayers().stream()
                    .filter(p -> p.getId().getValue().equals(existingPrayerUuid))
                    .findFirst().orElseThrow();
            assertThat(updated.getPrayerRequest()).isEqualTo("수정된 기도");
            assertThat(updated.isAnswered()).isTrue();
        }

        @Test
        @DisplayName("새 기도제목은 isAnswered=false로 생성된다")
        void shouldCreateNewPrayerWithIsAnsweredFalse() {
            UpdateVisitMemberCommand.PrayerUpdateCommand newPrayer =
                    new UpdateVisitMemberCommand.PrayerUpdateCommand(
                            null, "새 기도", "새 설명");

            UpdateVisitMemberCommand command = new UpdateVisitMemberCommand(
                    visitId, vmId, "스토리",
                    List.of(newPrayer));

            when(visitPort.findDetailById(visitId)).thenReturn(Optional.of(visitWithMember));
            when(visitPort.save(any(Visit.class))).thenAnswer(inv -> inv.getArgument(0));

            VisitMember result = visitCommandService.updateVisitMember(command, churchId);

            Prayer newP = result.getPrayers().stream()
                    .filter(p -> !p.getId().getValue().equals(existingPrayer.getId().getValue()))
                    .findFirst().orElseThrow();
            assertThat(newP.isAnswered()).isFalse();
            assertThat(newP.getPrayerRequest()).isEqualTo("새 기도");
        }

        @Test
        @DisplayName("요청에 없는 기도제목은 soft delete된다")
        void shouldSoftDeletePrayersNotInRequest() {
            UpdateVisitMemberCommand command = new UpdateVisitMemberCommand(
                    visitId, vmId, "스토리", List.of());

            when(visitPort.findDetailById(visitId)).thenReturn(Optional.of(visitWithMember));
            when(visitPort.save(any(Visit.class))).thenAnswer(inv -> inv.getArgument(0));

            VisitMember result = visitCommandService.updateVisitMember(command, churchId);

            Prayer deletedPrayer = result.getPrayers().stream()
                    .filter(p -> p.getId().equals(existingPrayer.getId()))
                    .findFirst().orElseThrow();
            assertThat(deletedPrayer.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("스토리가 업데이트된다")
        void shouldUpdateStory() {
            UpdateVisitMemberCommand command = new UpdateVisitMemberCommand(
                    visitId, vmId, "새로운 스토리", List.of());

            when(visitPort.findDetailById(visitId)).thenReturn(Optional.of(visitWithMember));
            when(visitPort.save(any(Visit.class))).thenAnswer(inv -> inv.getArgument(0));

            VisitMember result = visitCommandService.updateVisitMember(command, churchId);

            assertThat(result.getStory()).isEqualTo("새로운 스토리");
        }

        @Test
        @DisplayName("다른 교회의 심방 멤버는 수정할 수 없다")
        void shouldThrowWhenChurchDoesNotMatch() {
            UpdateVisitMemberCommand command = new UpdateVisitMemberCommand(
                    visitId, vmId, "스토리", List.of());

            when(visitPort.findDetailById(visitId)).thenReturn(Optional.of(visitWithMember));

            assertThatThrownBy(() -> visitCommandService.updateVisitMember(command, otherChurchId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Access denied");
        }

        @Test
        @DisplayName("심방이 존재하지 않으면 IllegalArgumentException이 발생한다")
        void shouldThrowWhenVisitNotFound() {
            UpdateVisitMemberCommand command = new UpdateVisitMemberCommand(
                    visitId, vmId, "스토리", List.of());

            when(visitPort.findDetailById(visitId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> visitCommandService.updateVisitMember(command, churchId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Visit not found");
        }

        @Test
        @DisplayName("심방 멤버가 존재하지 않으면 IllegalArgumentException이 발생한다")
        void shouldThrowWhenVisitMemberNotFound() {
            VisitMemberId unknownVmId = VisitMemberId.from(UUID.randomUUID());

            UpdateVisitMemberCommand command = new UpdateVisitMemberCommand(
                    visitId, unknownVmId, "스토리", List.of());

            when(visitPort.findDetailById(visitId)).thenReturn(Optional.of(visitWithMember));

            assertThatThrownBy(() -> visitCommandService.updateVisitMember(command, churchId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Visit member not found");
        }
    }
}

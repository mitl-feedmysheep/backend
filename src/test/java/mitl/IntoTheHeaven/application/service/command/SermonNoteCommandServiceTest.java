package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.CreateSermonNoteCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateSermonNoteCommand;
import mitl.IntoTheHeaven.application.port.out.SermonNotePort;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.SermonNote;
import mitl.IntoTheHeaven.domain.model.SermonNoteId;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SermonNoteCommandServiceTest {

    @Mock
    private SermonNotePort sermonNotePort;

    @InjectMocks
    private SermonNoteCommandService sermonNoteCommandService;

    private MemberId memberId;
    private SermonNoteId sermonNoteId;
    private UUID sermonNoteUuid;
    private SermonNote existingSermonNote;

    @BeforeEach
    void setUp() {
        memberId = MemberId.from(UUID.randomUUID());
        sermonNoteUuid = UUID.randomUUID();
        sermonNoteId = SermonNoteId.from(sermonNoteUuid);
        existingSermonNote = SermonNote.builder()
                .id(sermonNoteId)
                .memberId(memberId)
                .title("기존 제목")
                .sermonDate(LocalDate.of(2025, 6, 1))
                .preacher("김목사")
                .serviceType("주일예배")
                .scripture("요한복음 3:16")
                .content("기존 내용")
                .build();
    }

    @Nested
    @DisplayName("create - 설교 노트 생성")
    class CreateTests {

        @Test
        @DisplayName("UUID가 생성되고 모든 커맨드 필드가 매핑된다")
        void shouldCreateWithAllFields() {
            CreateSermonNoteCommand command = new CreateSermonNoteCommand(
                    memberId, "제목", LocalDate.of(2025, 7, 6),
                    "이목사", "수요예배", "마태복음 5:1", "내용");

            when(sermonNotePort.save(any(SermonNote.class))).thenAnswer(inv -> inv.getArgument(0));

            SermonNote result = sermonNoteCommandService.create(command);

            ArgumentCaptor<SermonNote> captor = ArgumentCaptor.forClass(SermonNote.class);
            verify(sermonNotePort).save(captor.capture());
            SermonNote saved = captor.getValue();

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getMemberId()).isEqualTo(memberId);
            assertThat(saved.getTitle()).isEqualTo("제목");
            assertThat(saved.getSermonDate()).isEqualTo(LocalDate.of(2025, 7, 6));
            assertThat(saved.getPreacher()).isEqualTo("이목사");
            assertThat(saved.getServiceType()).isEqualTo("수요예배");
            assertThat(saved.getScripture()).isEqualTo("마태복음 5:1");
            assertThat(saved.getContent()).isEqualTo("내용");
        }
    }

    @Nested
    @DisplayName("update - 설교 노트 업데이트")
    class UpdateTests {

        @Test
        @DisplayName("설교 노트의 6개 필드가 모두 업데이트된다")
        void shouldUpdateAllSixFields() {
            UpdateSermonNoteCommand command = new UpdateSermonNoteCommand(
                    sermonNoteId, "새 제목", LocalDate.of(2025, 8, 10),
                    "박목사", "새벽예배", "로마서 8:28", "새 내용");

            when(sermonNotePort.findById(sermonNoteUuid)).thenReturn(Optional.of(existingSermonNote));
            when(sermonNotePort.save(any(SermonNote.class))).thenAnswer(inv -> inv.getArgument(0));

            SermonNote result = sermonNoteCommandService.update(command);

            ArgumentCaptor<SermonNote> captor = ArgumentCaptor.forClass(SermonNote.class);
            verify(sermonNotePort).save(captor.capture());
            SermonNote saved = captor.getValue();

            assertThat(saved.getTitle()).isEqualTo("새 제목");
            assertThat(saved.getSermonDate()).isEqualTo(LocalDate.of(2025, 8, 10));
            assertThat(saved.getPreacher()).isEqualTo("박목사");
            assertThat(saved.getServiceType()).isEqualTo("새벽예배");
            assertThat(saved.getScripture()).isEqualTo("로마서 8:28");
            assertThat(saved.getContent()).isEqualTo("새 내용");
        }

        @Test
        @DisplayName("설교 노트가 존재하지 않으면 RuntimeException이 발생한다")
        void shouldThrowWhenNotFound() {
            UpdateSermonNoteCommand command = new UpdateSermonNoteCommand(
                    sermonNoteId, "제목", LocalDate.now(), "목사", "예배", "구절", "내용");

            when(sermonNotePort.findById(sermonNoteUuid)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sermonNoteCommandService.update(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Sermon note not found");
        }
    }

    @Nested
    @DisplayName("delete - 설교 노트 삭제")
    class DeleteTests {

        @Test
        @DisplayName("설교 노트를 soft delete하고 저장한다")
        void shouldSoftDeleteAndSave() {
            when(sermonNotePort.findById(sermonNoteUuid)).thenReturn(Optional.of(existingSermonNote));
            when(sermonNotePort.save(any(SermonNote.class))).thenAnswer(inv -> inv.getArgument(0));

            sermonNoteCommandService.delete(sermonNoteId);

            ArgumentCaptor<SermonNote> captor = ArgumentCaptor.forClass(SermonNote.class);
            verify(sermonNotePort).save(captor.capture());
            assertThat(captor.getValue().getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("설교 노트가 존재하지 않으면 RuntimeException이 발생한다")
        void shouldThrowWhenNotFound() {
            when(sermonNotePort.findById(sermonNoteUuid)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sermonNoteCommandService.delete(sermonNoteId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Sermon note not found");

            verify(sermonNotePort, never()).save(any());
        }
    }
}

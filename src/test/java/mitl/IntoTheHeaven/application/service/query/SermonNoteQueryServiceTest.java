package mitl.IntoTheHeaven.application.service.query;

import mitl.IntoTheHeaven.application.port.out.SermonNotePort;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.SermonNote;
import mitl.IntoTheHeaven.domain.model.SermonNoteId;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SermonNoteQueryServiceTest {

    @Mock
    private SermonNotePort sermonNotePort;

    @InjectMocks
    private SermonNoteQueryService sermonNoteQueryService;

    private SermonNote createSermonNote(SermonNoteId id, MemberId memberId, String title) {
        return SermonNote.builder()
                .id(id)
                .memberId(memberId)
                .title(title)
                .sermonDate(LocalDate.of(2025, 6, 15))
                .preacher("김목사")
                .serviceType("주일예배")
                .scripture("요한복음 3:16")
                .content("설교 내용")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("getMySermonNotes")
    class GetMySermonNotes {

        @Test
        @DisplayName("내 설교노트 목록 조회")
        void shouldReturnSermonNotes() {
            MemberId memberId = MemberId.from(UUID.randomUUID());

            List<SermonNote> notes = List.of(
                    createSermonNote(SermonNoteId.from(UUID.randomUUID()), memberId, "은혜의 설교"),
                    createSermonNote(SermonNoteId.from(UUID.randomUUID()), memberId, "믿음의 설교")
            );

            when(sermonNotePort.findAllByMemberId(memberId.getValue())).thenReturn(notes);

            List<SermonNote> result = sermonNoteQueryService.getMySermonNotes(memberId);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getTitle()).isEqualTo("은혜의 설교");
            verify(sermonNotePort).findAllByMemberId(memberId.getValue());
        }

        @Test
        @DisplayName("설교노트가 없으면 빈 리스트 반환")
        void shouldReturnEmptyListWhenNoNotes() {
            MemberId memberId = MemberId.from(UUID.randomUUID());

            when(sermonNotePort.findAllByMemberId(memberId.getValue())).thenReturn(List.of());

            List<SermonNote> result = sermonNoteQueryService.getMySermonNotes(memberId);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getSermonNoteById")
    class GetSermonNoteById {

        @Test
        @DisplayName("설교노트 ID로 조회 성공")
        void shouldReturnSermonNoteWhenFound() {
            SermonNoteId noteId = SermonNoteId.from(UUID.randomUUID());
            MemberId memberId = MemberId.from(UUID.randomUUID());
            SermonNote note = createSermonNote(noteId, memberId, "주일 설교");

            when(sermonNotePort.findById(noteId.getValue())).thenReturn(Optional.of(note));

            SermonNote result = sermonNoteQueryService.getSermonNoteById(noteId);

            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("주일 설교");
            assertThat(result.getPreacher()).isEqualTo("김목사");
        }

        @Test
        @DisplayName("존재하지 않는 설교노트 조회 시 RuntimeException 발생")
        void shouldThrowExceptionWhenNotFound() {
            SermonNoteId noteId = SermonNoteId.from(UUID.randomUUID());

            when(sermonNotePort.findById(noteId.getValue())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sermonNoteQueryService.getSermonNoteById(noteId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Sermon note not found");
        }
    }

    @Nested
    @DisplayName("getMyServiceTypes")
    class GetMyServiceTypes {

        @Test
        @DisplayName("내 예배 유형 목록 조회")
        void shouldReturnDistinctServiceTypes() {
            MemberId memberId = MemberId.from(UUID.randomUUID());
            List<String> serviceTypes = List.of("주일예배", "수요예배", "금요기도회");

            when(sermonNotePort.findDistinctServiceTypesByMemberId(memberId.getValue()))
                    .thenReturn(serviceTypes);

            List<String> result = sermonNoteQueryService.getMyServiceTypes(memberId);

            assertThat(result).hasSize(3);
            assertThat(result).containsExactly("주일예배", "수요예배", "금요기도회");
            verify(sermonNotePort).findDistinctServiceTypesByMemberId(memberId.getValue());
        }

        @Test
        @DisplayName("예배 유형이 없으면 빈 리스트 반환")
        void shouldReturnEmptyListWhenNoServiceTypes() {
            MemberId memberId = MemberId.from(UUID.randomUUID());

            when(sermonNotePort.findDistinctServiceTypesByMemberId(memberId.getValue()))
                    .thenReturn(List.of());

            List<String> result = sermonNoteQueryService.getMyServiceTypes(memberId);

            assertThat(result).isEmpty();
        }
    }
}

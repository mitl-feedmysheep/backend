package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.SermonNoteCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.CreateSermonNoteCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateSermonNoteCommand;
import mitl.IntoTheHeaven.application.port.out.SermonNotePort;
import mitl.IntoTheHeaven.domain.model.SermonNote;
import mitl.IntoTheHeaven.domain.model.SermonNoteId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class SermonNoteCommandService implements SermonNoteCommandUseCase {

    private final SermonNotePort sermonNotePort;

    @Override
    public SermonNote create(CreateSermonNoteCommand command) {
        SermonNote sermonNote = SermonNote.builder()
                .id(SermonNoteId.from(UUID.randomUUID()))
                .memberId(command.getMemberId())
                .title(command.getTitle())
                .sermonDate(command.getSermonDate())
                .preacher(command.getPreacher())
                .serviceType(command.getServiceType())
                .scripture(command.getScripture())
                .content(command.getContent())
                .build();

        return sermonNotePort.save(sermonNote);
    }

    @Override
    public SermonNote update(UpdateSermonNoteCommand command) {
        SermonNote sermonNote = sermonNotePort.findById(command.getSermonNoteId().getValue())
                .orElseThrow(() -> new RuntimeException("Sermon note not found"));

        SermonNote updated = sermonNote.update(
                command.getTitle(),
                command.getSermonDate(),
                command.getPreacher(),
                command.getServiceType(),
                command.getScripture(),
                command.getContent()
        );

        return sermonNotePort.save(updated);
    }

    @Override
    public void delete(SermonNoteId sermonNoteId) {
        SermonNote sermonNote = sermonNotePort.findById(sermonNoteId.getValue())
                .orElseThrow(() -> new RuntimeException("Sermon note not found"));
        SermonNote deleted = sermonNote.delete();
        sermonNotePort.save(deleted);
    }
}

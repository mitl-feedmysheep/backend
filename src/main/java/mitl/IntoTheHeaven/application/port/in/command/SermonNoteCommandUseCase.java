package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.CreateSermonNoteCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateSermonNoteCommand;
import mitl.IntoTheHeaven.domain.model.SermonNote;
import mitl.IntoTheHeaven.domain.model.SermonNoteId;

public interface SermonNoteCommandUseCase {

    SermonNote create(CreateSermonNoteCommand command);

    SermonNote update(UpdateSermonNoteCommand command);

    void delete(SermonNoteId sermonNoteId);
}

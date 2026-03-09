package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.SermonNote;
import mitl.IntoTheHeaven.domain.model.SermonNoteId;

import java.util.List;

public interface SermonNoteQueryUseCase {

    List<SermonNote> getMySermonNotes(MemberId memberId);

    SermonNote getSermonNoteById(SermonNoteId sermonNoteId);

    List<String> getMyServiceTypes(MemberId memberId);
}

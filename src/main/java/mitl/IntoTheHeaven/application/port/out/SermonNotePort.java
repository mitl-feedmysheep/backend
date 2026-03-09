package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.SermonNote;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SermonNotePort {

    List<SermonNote> findAllByMemberId(UUID memberId);

    Optional<SermonNote> findById(UUID sermonNoteId);

    SermonNote save(SermonNote sermonNote);

    List<String> findDistinctServiceTypesByMemberId(UUID memberId);
}

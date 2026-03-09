package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.query.SermonNoteQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.SermonNotePort;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.SermonNote;
import mitl.IntoTheHeaven.domain.model.SermonNoteId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SermonNoteQueryService implements SermonNoteQueryUseCase {

    private final SermonNotePort sermonNotePort;

    @Override
    public List<SermonNote> getMySermonNotes(MemberId memberId) {
        return sermonNotePort.findAllByMemberId(memberId.getValue());
    }

    @Override
    public SermonNote getSermonNoteById(SermonNoteId sermonNoteId) {
        return sermonNotePort.findById(sermonNoteId.getValue())
                .orElseThrow(() -> new RuntimeException("Sermon note not found"));
    }

    @Override
    public List<String> getMyServiceTypes(MemberId memberId) {
        return sermonNotePort.findDistinctServiceTypesByMemberId(memberId.getValue());
    }
}

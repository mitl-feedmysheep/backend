package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import mitl.IntoTheHeaven.adapter.in.web.dto.sermonnote.UpdateSermonNoteRequest;
import mitl.IntoTheHeaven.domain.model.SermonNoteId;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UpdateSermonNoteCommand {

    private SermonNoteId sermonNoteId;
    private String title;
    private LocalDate sermonDate;
    private String preacher;
    private String serviceType;
    private String scripture;
    private String content;

    public static UpdateSermonNoteCommand from(UpdateSermonNoteRequest request, SermonNoteId sermonNoteId) {
        return new UpdateSermonNoteCommand(
                sermonNoteId,
                request.getTitle(),
                request.getSermonDate(),
                request.getPreacher(),
                request.getServiceType(),
                request.getScripture(),
                request.getContent()
        );
    }
}

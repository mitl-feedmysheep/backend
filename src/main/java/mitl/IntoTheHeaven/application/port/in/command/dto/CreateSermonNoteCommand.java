package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import mitl.IntoTheHeaven.adapter.in.web.dto.sermonnote.CreateSermonNoteRequest;
import mitl.IntoTheHeaven.domain.model.MemberId;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CreateSermonNoteCommand {

    private MemberId memberId;
    private String title;
    private LocalDate sermonDate;
    private String preacher;
    private String serviceType;
    private String scripture;
    private String content;

    public static CreateSermonNoteCommand from(CreateSermonNoteRequest request, MemberId memberId) {
        return new CreateSermonNoteCommand(
                memberId,
                request.getTitle(),
                request.getSermonDate(),
                request.getPreacher(),
                request.getServiceType(),
                request.getScripture(),
                request.getContent()
        );
    }
}

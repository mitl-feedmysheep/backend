package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.domain.model.EducationProgramId;

@Getter
@RequiredArgsConstructor
public class UpdateEducationProgramCommand {

    private final EducationProgramId programId;
    private final String name;
    private final String description;
    private final int totalWeeks;
}

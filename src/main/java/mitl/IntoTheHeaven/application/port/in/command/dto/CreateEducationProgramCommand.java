package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.domain.model.GroupId;

@Getter
@RequiredArgsConstructor
public class CreateEducationProgramCommand {

    private final GroupId groupId;
    private final String name;
    private final String description;
    private final int totalWeeks;
}

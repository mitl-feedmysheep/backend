package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.CreateEducationProgramCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.GraduateMemberCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.RecordEducationProgressCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateEducationProgramCommand;
import mitl.IntoTheHeaven.domain.model.EducationProgram;
import mitl.IntoTheHeaven.domain.model.EducationProgress;
import mitl.IntoTheHeaven.domain.model.EducationProgressId;

public interface EducationCommandUseCase {

    EducationProgram createProgram(CreateEducationProgramCommand command);

    EducationProgram updateProgram(UpdateEducationProgramCommand command);

    EducationProgress recordProgress(RecordEducationProgressCommand command);

    void removeProgress(EducationProgressId progressId);

    void graduateMember(GraduateMemberCommand command);
}

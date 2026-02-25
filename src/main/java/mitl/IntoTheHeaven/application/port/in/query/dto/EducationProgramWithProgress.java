package mitl.IntoTheHeaven.application.port.in.query.dto;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.model.EducationProgram;
import mitl.IntoTheHeaven.domain.model.EducationProgress;

import java.util.List;

@Getter
@Builder
public class EducationProgramWithProgress {

    private final EducationProgram program;
    private final List<EducationProgress> progressList;
}

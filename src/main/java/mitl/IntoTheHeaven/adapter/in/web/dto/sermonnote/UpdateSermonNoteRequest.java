package mitl.IntoTheHeaven.adapter.in.web.dto.sermonnote;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateSermonNoteRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be less than 100 characters")
    private String title;

    @NotNull(message = "Sermon date is required")
    private LocalDate sermonDate;

    @Size(max = 50, message = "Preacher name must be less than 50 characters")
    private String preacher;

    @Size(max = 50, message = "Service type must be less than 50 characters")
    private String serviceType;

    @Size(max = 200, message = "Scripture must be less than 200 characters")
    private String scripture;

    @NotBlank(message = "Content is required")
    private String content;
}

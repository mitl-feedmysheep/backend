package mitl.IntoTheHeaven.adapter.in.web.dto.gathering;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
public class UpdateGatheringRequest {

    @Size(max = 50, message = "Name must be less than 50 characters")
    private String name;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    private LocalDate date;

    private OffsetDateTime startedAt;

    private OffsetDateTime endedAt;

    @Size(max = 100, message = "Place must be less than 100 characters")
    private String place;

    @Size(max = 100, message = "Leader comment must be less than 100 characters")
    private String leaderComment;

    @Size(max = 100, message = "Admin comment must be less than 100 characters")
    private String adminComment;
}


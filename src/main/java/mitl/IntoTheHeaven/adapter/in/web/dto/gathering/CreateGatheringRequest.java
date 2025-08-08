package mitl.IntoTheHeaven.adapter.in.web.dto.gathering;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateGatheringRequest {

    @NotNull(message = "Group ID is required")
    private UUID groupId;

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must be less than 50 characters")
    private String name;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Start time is required")
    private OffsetDateTime startedAt; // Keep offset (e.g., Z)

    @NotNull(message = "End time is required")
    private OffsetDateTime endedAt; // Keep offset (e.g., Z)

    @NotBlank(message = "Place is required")
    @Size(max = 100, message = "Place must be less than 100 characters")
    private String place;
} 
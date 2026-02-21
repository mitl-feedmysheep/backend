package mitl.IntoTheHeaven.adapter.in.web.dto.church;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.enums.RequestStatus;
import mitl.IntoTheHeaven.domain.model.ChurchMemberRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class JoinRequestResponse {
    private final UUID id;
    private final UUID churchId;
    private final String churchName;
    private final RequestStatus status;
    private final LocalDateTime createdAt;

    @Builder
    public JoinRequestResponse(UUID id, UUID churchId, String churchName, RequestStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.churchId = churchId;
        this.churchName = churchName;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static JoinRequestResponse from(ChurchMemberRequest request) {
        return JoinRequestResponse.builder()
                .id(request.getId().getValue())
                .churchId(request.getChurchId().getValue())
                .churchName(request.getChurchName())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .build();
    }

    public static List<JoinRequestResponse> from(List<ChurchMemberRequest> requests) {
        return requests.stream()
                .map(JoinRequestResponse::from)
                .toList();
    }
}

package mitl.IntoTheHeaven.adapter.in.web.dto.department;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.model.Department;

import java.util.List;
import java.util.UUID;

@Getter
public class DepartmentResponse {
    private final UUID id;
    private final String name;
    private final String description;
    private final UUID churchId;
    private final boolean isDefault;

    @Builder
    public DepartmentResponse(UUID id, String name, String description, UUID churchId, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.churchId = churchId;
        this.isDefault = isDefault;
    }

    public static DepartmentResponse from(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId().getValue())
                .name(department.getName())
                .description(department.getDescription())
                .churchId(department.getChurchId().getValue())
                .isDefault(department.isDefault())
                .build();
    }

    public static List<DepartmentResponse> from(List<Department> departments) {
        return departments.stream()
                .map(DepartmentResponse::from)
                .toList();
    }
}

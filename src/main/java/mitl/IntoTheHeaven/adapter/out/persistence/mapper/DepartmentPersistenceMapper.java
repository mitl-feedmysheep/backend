package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.DepartmentJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.DepartmentMemberJpaEntity;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.Department;
import mitl.IntoTheHeaven.domain.model.DepartmentId;
import mitl.IntoTheHeaven.domain.model.DepartmentMember;
import mitl.IntoTheHeaven.domain.model.DepartmentMemberId;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DepartmentPersistenceMapper {

    private final MemberPersistenceMapper memberPersistenceMapper;

    public Department toDomain(DepartmentJpaEntity entity) {
        return Department.builder()
                .id(DepartmentId.from(entity.getId()))
                .name(entity.getName())
                .description(entity.getDescription())
                .churchId(ChurchId.from(entity.getChurch().getId()))
                .isDefault(Boolean.TRUE.equals(entity.getIsDefault()))
                .build();
    }

    public DepartmentJpaEntity toEntity(Department domain) {
        return DepartmentJpaEntity.builder()
                .id(domain.getId().getValue())
                .name(domain.getName())
                .description(domain.getDescription())
                .isDefault(domain.isDefault())
                .church(ChurchJpaEntity.builder().id(domain.getChurchId().getValue()).build())
                .build();
    }

    public DepartmentMember toDepartmentMemberDomain(DepartmentMemberJpaEntity entity) {
        return DepartmentMember.builder()
                .id(DepartmentMemberId.from(entity.getId()))
                .departmentId(DepartmentId.from(entity.getDepartment().getId()))
                .member(memberPersistenceMapper.toDomain(entity.getMember()))
                .role(entity.getRole())
                .status(entity.getStatus())
                .build();
    }
}

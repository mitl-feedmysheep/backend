package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.DepartmentJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.DepartmentMemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.DepartmentPersistenceMapper;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.DepartmentJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.DepartmentMemberJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.GroupJpaRepository;
import mitl.IntoTheHeaven.application.port.out.DepartmentPort;
import mitl.IntoTheHeaven.domain.enums.DepartmentMemberStatus;
import mitl.IntoTheHeaven.domain.enums.DepartmentRole;
import mitl.IntoTheHeaven.domain.model.Department;
import mitl.IntoTheHeaven.domain.model.DepartmentMember;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DepartmentPersistenceAdapter implements DepartmentPort {

    private final DepartmentJpaRepository departmentJpaRepository;
    private final DepartmentMemberJpaRepository departmentMemberJpaRepository;
    private final GroupJpaRepository groupJpaRepository;
    private final DepartmentPersistenceMapper departmentPersistenceMapper;

    @Override
    public Department save(Department department) {
        DepartmentJpaEntity entity = departmentPersistenceMapper.toEntity(department);
        DepartmentJpaEntity saved = departmentJpaRepository.save(entity);
        return departmentPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Department> findById(UUID departmentId) {
        return departmentJpaRepository.findById(departmentId)
                .map(departmentPersistenceMapper::toDomain);
    }

    @Override
    public List<Department> findByChurchId(UUID churchId) {
        return departmentJpaRepository.findAllByChurchId(churchId).stream()
                .map(departmentPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID departmentId) {
        departmentJpaRepository.findById(departmentId).ifPresent(entity -> {
            // Soft delete
            DepartmentJpaEntity deleted = DepartmentJpaEntity.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .description(entity.getDescription())
                    .church(entity.getChurch())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .deletedAt(LocalDateTime.now())
                    .build();
            departmentJpaRepository.save(deleted);
        });
    }

    @Override
    public DepartmentMember saveDepartmentMember(DepartmentMember departmentMember, UUID departmentId, UUID memberId) {
        DepartmentMemberJpaEntity entity = DepartmentMemberJpaEntity.builder()
                .id(departmentMember.getId().getValue())
                .department(DepartmentJpaEntity.builder().id(departmentId).build())
                .member(MemberJpaEntity.builder().id(memberId).build())
                .role(departmentMember.getRole())
                .status(departmentMember.getStatus())
                .build();
        DepartmentMemberJpaEntity saved = departmentMemberJpaRepository.save(entity);
        return departmentPersistenceMapper.toDepartmentMemberDomain(saved);
    }

    @Override
    public List<DepartmentMember> findDepartmentMembersByDepartmentId(UUID departmentId) {
        return departmentMemberJpaRepository.findByDepartmentId(departmentId).stream()
                .map(departmentPersistenceMapper::toDepartmentMemberDomain)
                .toList();
    }

    @Override
    public List<DepartmentMember> findActiveDepartmentMembersByDepartmentId(UUID departmentId) {
        return departmentMemberJpaRepository.findByDepartmentIdAndStatus(departmentId, DepartmentMemberStatus.ACTIVE).stream()
                .map(departmentPersistenceMapper::toDepartmentMemberDomain)
                .toList();
    }

    @Override
    public Optional<DepartmentMember> findDepartmentMemberByDepartmentIdAndMemberId(UUID departmentId, UUID memberId) {
        return departmentMemberJpaRepository.findByDepartment_IdAndMember_Id(departmentId, memberId)
                .map(departmentPersistenceMapper::toDepartmentMemberDomain);
    }

    @Override
    public List<DepartmentMember> findDepartmentMembersByMemberId(UUID memberId) {
        return departmentMemberJpaRepository.findByMember_Id(memberId).stream()
                .map(departmentPersistenceMapper::toDepartmentMemberDomain)
                .toList();
    }

    @Override
    public List<DepartmentMember> findDepartmentMembersByMemberIdAndChurchId(UUID memberId, UUID churchId) {
        return departmentMemberJpaRepository.findByMember_IdAndDepartment_Church_Id(memberId, churchId).stream()
                .map(departmentPersistenceMapper::toDepartmentMemberDomain)
                .toList();
    }

    @Override
    public DepartmentMember updateDepartmentMemberRole(UUID departmentMemberId, DepartmentRole newRole) {
        DepartmentMemberJpaEntity entity = departmentMemberJpaRepository.findById(departmentMemberId)
                .orElseThrow(() -> new RuntimeException("DepartmentMember not found: " + departmentMemberId));
        entity.setRole(newRole);
        entity = departmentMemberJpaRepository.save(entity);
        return departmentPersistenceMapper.toDepartmentMemberDomain(entity);
    }

    @Override
    public DepartmentMember updateDepartmentMemberStatus(UUID departmentMemberId, DepartmentMemberStatus newStatus) {
        DepartmentMemberJpaEntity entity = departmentMemberJpaRepository.findById(departmentMemberId)
                .orElseThrow(() -> new RuntimeException("DepartmentMember not found: " + departmentMemberId));
        entity.setStatus(newStatus);
        entity = departmentMemberJpaRepository.save(entity);
        return departmentPersistenceMapper.toDepartmentMemberDomain(entity);
    }

    @Override
    public void deleteDepartmentMember(UUID departmentMemberId) {
        departmentMemberJpaRepository.findById(departmentMemberId).ifPresent(entity -> {
            DepartmentMemberJpaEntity deleted = DepartmentMemberJpaEntity.builder()
                    .id(entity.getId())
                    .department(entity.getDepartment())
                    .member(entity.getMember())
                    .role(entity.getRole())
                    .status(entity.getStatus())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .deletedAt(LocalDateTime.now())
                    .build();
            departmentMemberJpaRepository.save(deleted);
        });
    }

    @Override
    public long countActiveMembersByDepartmentId(UUID departmentId) {
        return departmentMemberJpaRepository.findByDepartmentIdAndStatus(departmentId, DepartmentMemberStatus.ACTIVE).size();
    }

    @Override
    public long countGroupsByDepartmentId(UUID departmentId) {
        return groupJpaRepository.findAllByDepartmentId(departmentId).size();
    }
}

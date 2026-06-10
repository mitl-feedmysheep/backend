package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.*;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.*;
import mitl.IntoTheHeaven.application.port.out.ReadingCompletionHistoryPort;
import mitl.IntoTheHeaven.application.port.out.ReadingPlanPort;
import mitl.IntoTheHeaven.domain.model.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReadingPlanPersistenceAdapter implements ReadingPlanPort, ReadingCompletionHistoryPort {

    private final ReadingPlanJpaRepository readingPlanJpaRepository;
    private final ReadingPlanDayJpaRepository readingPlanDayJpaRepository;
    private final DepartmentReadingPlanJpaRepository departmentReadingPlanJpaRepository;
    private final ReadingCompletionHistoryJpaRepository readingCompletionHistoryJpaRepository;
    private final DepartmentJpaRepository departmentJpaRepository;
    private final MemberJpaRepository memberJpaRepository;

    // ── ReadingPlanPort ──────────────────────────────────────────────────────

    @Override
    public ReadingPlan save(ReadingPlan plan) {
        ReadingPlanJpaEntity entity = ReadingPlanJpaEntity.builder()
                .id(plan.getId().getValue())
                .title(plan.getTitle())
                .startDate(plan.getStartDate())
                .totalDays(plan.getTotalDays())
                .deletedAt(plan.getDeletedAt())
                .build();
        return toPlanDomain(readingPlanJpaRepository.save(entity));
    }

    @Override
    public Optional<ReadingPlan> findById(UUID id) {
        return readingPlanJpaRepository.findById(id).map(this::toPlanDomain);
    }

    @Override
    public ReadingPlanDay saveDay(ReadingPlanDay day) {
        ReadingPlanJpaEntity planRef = readingPlanJpaRepository.getReferenceById(day.getReadingPlanId().getValue());
        ReadingPlanDayJpaEntity entity = ReadingPlanDayJpaEntity.builder()
                .id(day.getId().getValue())
                .readingPlan(planRef)
                .readingDate(day.getReadingDate())
                .dayNumber(day.getDayNumber())
                .readingRange(day.getReadingRange())
                .youtubeUrl(day.getYoutubeUrl())
                .description(day.getDescription())
                .deletedAt(day.getDeletedAt())
                .build();
        return toDayDomain(readingPlanDayJpaRepository.save(entity));
    }

    @Override
    public List<ReadingPlanDay> saveDays(List<ReadingPlanDay> days) {
        List<ReadingPlanDayJpaEntity> entities = days.stream()
                .map(day -> {
                    ReadingPlanJpaEntity planRef = readingPlanJpaRepository.getReferenceById(day.getReadingPlanId().getValue());
                    return (ReadingPlanDayJpaEntity) ReadingPlanDayJpaEntity.builder()
                            .id(day.getId().getValue())
                            .readingPlan(planRef)
                            .readingDate(day.getReadingDate())
                            .dayNumber(day.getDayNumber())
                            .readingRange(day.getReadingRange())
                            .youtubeUrl(day.getYoutubeUrl())
                            .description(day.getDescription())
                            .build();
                })
                .collect(java.util.stream.Collectors.toList());
        return readingPlanDayJpaRepository.saveAll(entities).stream()
                .map(this::toDayDomain)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Optional<ReadingPlanDay> findDayById(UUID id) {
        return readingPlanDayJpaRepository.findById(id).map(this::toDayDomain);
    }

    @Override
    public Optional<ReadingPlanDay> findDayByPlanIdAndDate(UUID planId, LocalDate date) {
        return readingPlanDayJpaRepository.findByReadingPlanIdAndReadingDate(planId, date)
                .map(this::toDayDomain);
    }

    @Override
    public List<ReadingPlanDay> findDaysByPlanId(UUID planId) {
        return readingPlanDayJpaRepository.findByReadingPlanIdOrderByDayNumberAsc(planId)
                .stream().map(this::toDayDomain).toList();
    }

    @Override
    public DepartmentReadingPlan saveMapping(DepartmentReadingPlan mapping) {
        DepartmentJpaEntity deptRef = departmentJpaRepository.getReferenceById(mapping.getDepartmentId().getValue());
        ReadingPlanJpaEntity planRef = readingPlanJpaRepository.getReferenceById(mapping.getReadingPlanId().getValue());
        DepartmentReadingPlanJpaEntity entity = DepartmentReadingPlanJpaEntity.builder()
                .id(mapping.getId().getValue())
                .department(deptRef)
                .readingPlan(planRef)
                .startDate(mapping.getStartDate())
                .endDate(mapping.getEndDate())
                .deletedAt(mapping.getDeletedAt())
                .build();
        return toMappingDomain(departmentReadingPlanJpaRepository.save(entity));
    }

    @Override
    public Optional<DepartmentReadingPlan> findActiveMappingByDepartmentId(UUID departmentId) {
        return departmentReadingPlanJpaRepository
                .findActiveByDepartmentIdAndDate(departmentId, LocalDate.now())
                .map(this::toMappingDomain);
    }

    @Override
    public List<DepartmentReadingPlan> findAllActiveMappings() {
        return departmentReadingPlanJpaRepository.findAllActiveByDate(LocalDate.now())
                .stream().map(this::toMappingDomain).toList();
    }

    @Override
    public void deleteMapping(UUID mappingId) {
        departmentReadingPlanJpaRepository.deleteById(mappingId);
    }

    // ── ReadingCompletionHistoryPort ─────────────────────────────────────────

    @Override
    public ReadingCompletionHistory save(ReadingCompletionHistory history) {
        DepartmentReadingPlanJpaEntity deptPlanRef = departmentReadingPlanJpaRepository
                .getReferenceById(history.getDepartmentReadingPlanId().getValue());
        ReadingPlanDayJpaEntity dayRef = readingPlanDayJpaRepository
                .getReferenceById(history.getReadingPlanDayId().getValue());
        MemberJpaEntity memberRef = memberJpaRepository
                .getReferenceById(history.getMemberId().getValue());
        ReadingCompletionHistoryJpaEntity entity = ReadingCompletionHistoryJpaEntity.builder()
                .id(history.getId().getValue())
                .departmentReadingPlan(deptPlanRef)
                .readingPlanDay(dayRef)
                .member(memberRef)
                .completedAt(history.getCompletedAt())
                .build();
        return toHistoryDomain(readingCompletionHistoryJpaRepository.save(entity));
    }

    @Override
    public Optional<ReadingCompletionHistory> findByDeptPlanIdAndDayIdAndMemberId(UUID deptPlanId, UUID dayId, UUID memberId) {
        return readingCompletionHistoryJpaRepository
                .findByDepartmentReadingPlanIdAndReadingPlanDayIdAndMemberId(deptPlanId, dayId, memberId)
                .map(this::toHistoryDomain);
    }

    @Override
    public boolean existsByDeptPlanIdAndDayIdAndMemberId(UUID deptPlanId, UUID dayId, UUID memberId) {
        return readingCompletionHistoryJpaRepository
                .existsByDepartmentReadingPlanIdAndReadingPlanDayIdAndMemberId(deptPlanId, dayId, memberId);
    }

    @Override
    public void deleteByDeptPlanIdAndDayIdAndMemberId(UUID deptPlanId, UUID dayId, UUID memberId) {
        readingCompletionHistoryJpaRepository
                .findByDepartmentReadingPlanIdAndReadingPlanDayIdAndMemberId(deptPlanId, dayId, memberId)
                .ifPresent(readingCompletionHistoryJpaRepository::delete);
    }

    @Override
    public long countByDeptPlanIdAndMemberId(UUID deptPlanId, UUID memberId) {
        return readingCompletionHistoryJpaRepository.countByDepartmentReadingPlanIdAndMemberId(deptPlanId, memberId);
    }

    @Override
    public List<ReadingCompletionHistory> findByDeptPlanIdAndMemberId(UUID deptPlanId, UUID memberId) {
        return readingCompletionHistoryJpaRepository
                .findByDepartmentReadingPlanIdAndMemberId(deptPlanId, memberId)
                .stream().map(this::toHistoryDomain).toList();
    }

    @Override
    public List<UUID> findCompletedMemberIdsByDeptPlanIdAndDate(UUID deptPlanId, LocalDate date) {
        return readingCompletionHistoryJpaRepository.findMemberIdsByDeptPlanIdAndDate(deptPlanId, date);
    }

    // ── domain mappers ───────────────────────────────────────────────────────

    private ReadingPlan toPlanDomain(ReadingPlanJpaEntity e) {
        return ReadingPlan.builder()
                .id(ReadingPlanId.from(e.getId()))
                .title(e.getTitle())
                .startDate(e.getStartDate())
                .totalDays(e.getTotalDays())
                .createdAt(e.getCreatedAt())
                .deletedAt(e.getDeletedAt())
                .build();
    }

    private ReadingPlanDay toDayDomain(ReadingPlanDayJpaEntity e) {
        return ReadingPlanDay.builder()
                .id(ReadingPlanDayId.from(e.getId()))
                .readingPlanId(ReadingPlanId.from(e.getReadingPlan().getId()))
                .readingDate(e.getReadingDate())
                .dayNumber(e.getDayNumber())
                .readingRange(e.getReadingRange())
                .youtubeUrl(e.getYoutubeUrl())
                .description(e.getDescription())
                .createdAt(e.getCreatedAt())
                .deletedAt(e.getDeletedAt())
                .build();
    }

    private DepartmentReadingPlan toMappingDomain(DepartmentReadingPlanJpaEntity e) {
        return DepartmentReadingPlan.builder()
                .id(DepartmentReadingPlanId.from(e.getId()))
                .departmentId(DepartmentId.from(e.getDepartment().getId()))
                .readingPlanId(ReadingPlanId.from(e.getReadingPlan().getId()))
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .createdAt(e.getCreatedAt())
                .deletedAt(e.getDeletedAt())
                .build();
    }

    private ReadingCompletionHistory toHistoryDomain(ReadingCompletionHistoryJpaEntity e) {
        return ReadingCompletionHistory.builder()
                .id(ReadingCompletionHistoryId.from(e.getId()))
                .departmentReadingPlanId(DepartmentReadingPlanId.from(e.getDepartmentReadingPlan().getId()))
                .readingPlanDayId(ReadingPlanDayId.from(e.getReadingPlanDay().getId()))
                .memberId(MemberId.from(e.getMember().getId()))
                .completedAt(e.getCompletedAt())
                .build();
    }
}

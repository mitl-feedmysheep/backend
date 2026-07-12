package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.*;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.*;
import mitl.IntoTheHeaven.application.port.out.ReadingCompletionHistoryPort;
import mitl.IntoTheHeaven.application.port.out.ReadingPlanPort;
import mitl.IntoTheHeaven.domain.model.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReadingPlanPersistenceAdapter implements ReadingPlanPort, ReadingCompletionHistoryPort {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

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
                .churchId(plan.getChurchId())
                .title(plan.getTitle())
                .readingDays(plan.getReadingDays())
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
                .dayNumber(day.getDayNumber())
                .readingRange(day.getReadingRange())
                .audioUrl(day.getAudioUrl())
                .videoUrl(day.getVideoUrl())
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
                            .dayNumber(day.getDayNumber())
                            .readingRange(day.getReadingRange())
                            .audioUrl(day.getAudioUrl())
                            .videoUrl(day.getVideoUrl())
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
    public Optional<ReadingPlanDay> findDayByPlanIdAndDayNumber(UUID planId, int dayNumber) {
        return readingPlanDayJpaRepository.findByReadingPlanIdAndDayNumber(planId, dayNumber)
                .map(this::toDayDomain);
    }

    @Override
    public List<ReadingPlanDay> findDaysByPlanId(UUID planId) {
        return readingPlanDayJpaRepository.findByReadingPlanIdOrderByDayNumberAsc(planId)
                .stream().map(this::toDayDomain).toList();
    }

    @Override
    public int countDaysByPlanId(UUID planId) {
        return (int) readingPlanDayJpaRepository.countByReadingPlanId(planId);
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
                .findActiveByDepartmentIdAndDate(departmentId, LocalDate.now(KST))
                .map(this::toMappingDomain);
    }

    @Override
    public List<DepartmentReadingPlan> findAllActiveMappings() {
        return departmentReadingPlanJpaRepository.findAllActiveByDate(LocalDate.now(KST))
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
                .isCompleted(history.isCompleted())
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
    public void setIsCompleted(UUID deptPlanId, UUID dayId, UUID memberId, boolean isCompleted) {
        readingCompletionHistoryJpaRepository.updateIsCompleted(deptPlanId, dayId, memberId, isCompleted);
    }

    @Override
    public long countByDeptPlanIdAndMemberId(UUID deptPlanId, UUID memberId) {
        return readingCompletionHistoryJpaRepository
                .countByDepartmentReadingPlanIdAndMemberIdAndIsCompletedTrue(deptPlanId, memberId);
    }

    @Override
    public List<Integer> findCompletedDayNumbersByDeptPlanIdAndMemberId(UUID deptPlanId, UUID memberId) {
        return readingCompletionHistoryJpaRepository
                .findCompletedDayNumbersByDeptPlanIdAndMemberId(deptPlanId, memberId);
    }

    @Override
    public List<UUID> findCompletedMemberIdsByDeptPlanIdAndDate(UUID deptPlanId, LocalDate date) {
        return readingCompletionHistoryJpaRepository.findMemberIdsByDeptPlanIdAndDate(
                deptPlanId, date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }

    // ── domain mappers ───────────────────────────────────────────────────────

    private ReadingPlan toPlanDomain(ReadingPlanJpaEntity e) {
        return ReadingPlan.builder()
                .id(ReadingPlanId.from(e.getId()))
                .churchId(e.getChurchId())
                .title(e.getTitle())
                .readingDays(e.getReadingDays())
                .createdAt(e.getCreatedAt())
                .deletedAt(e.getDeletedAt())
                .build();
    }

    private ReadingPlanDay toDayDomain(ReadingPlanDayJpaEntity e) {
        return ReadingPlanDay.builder()
                .id(ReadingPlanDayId.from(e.getId()))
                .readingPlanId(ReadingPlanId.from(e.getReadingPlan().getId()))
                .dayNumber(e.getDayNumber())
                .readingRange(e.getReadingRange())
                .audioUrl(e.getAudioUrl())
                .videoUrl(e.getVideoUrl())
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
                .isCompleted(e.isCompleted())
                .build();
    }
}

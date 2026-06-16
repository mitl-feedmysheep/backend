package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import mitl.IntoTheHeaven.adapter.in.web.dto.*;
import mitl.IntoTheHeaven.application.port.in.command.ReadingCompletionCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.ReadingPlanCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.query.ReadingPlanQueryUseCase;
import mitl.IntoTheHeaven.domain.enums.DepartmentRole;
import mitl.IntoTheHeaven.domain.model.*;
import mitl.IntoTheHeaven.global.aop.RequireDepartmentRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "ReadingPlan", description = "리딩지저스 - 일일 성경 통독 APIs")
@RestController
@RequiredArgsConstructor
public class ReadingPlanController {

    private final ReadingPlanQueryUseCase readingPlanQueryUseCase;
    private final ReadingPlanCommandUseCase readingPlanCommandUseCase;
    private final ReadingCompletionCommandUseCase readingCompletionCommandUseCase;

    // ── 부서별 조회 (멤버) ────────────────────────────────────────────────────

    @Operation(summary = "리딩지저스 활성화 여부")
    @GetMapping("/departments/{departmentId}/reading-plan/status")
    public ResponseEntity<Boolean> getStatus(@PathVariable UUID departmentId) {
        return ResponseEntity.ok(
                readingPlanQueryUseCase.isReadingEnabled(DepartmentId.from(departmentId)));
    }

    @Operation(summary = "오늘 읽기 분량 조회")
    @GetMapping("/departments/{departmentId}/reading-plan/today")
    public ResponseEntity<TodayReadingResponse> getToday(
            @PathVariable UUID departmentId,
            @AuthenticationPrincipal String memberId) {
        DepartmentId deptId = DepartmentId.from(departmentId);
        ReadingPlanDay day = readingPlanQueryUseCase.getTodayReading(deptId);
        if (day == null) return ResponseEntity.noContent().build();

        String planTitle = readingPlanQueryUseCase.getActivePlanTitle(deptId);

        boolean completed = false;
        try {
            completed = readingPlanQueryUseCase.getMyProgress(
                    deptId, MemberId.from(UUID.fromString(memberId)))
                    .completedDates().contains(java.time.LocalDate.now());
        } catch (Exception ignored) {}

        return ResponseEntity.ok(TodayReadingResponse.from(day, completed, planTitle));
    }

    @Operation(summary = "오늘 부서 완독 인원 수")
    @GetMapping("/departments/{departmentId}/reading-plan/today/count")
    public ResponseEntity<Integer> getTodayCount(@PathVariable UUID departmentId) {
        return ResponseEntity.ok(
                readingPlanQueryUseCase.getTodayCompletionCount(DepartmentId.from(departmentId)));
    }

    @Operation(summary = "특정 날짜 읽기 분량 조회")
    @GetMapping("/departments/{departmentId}/reading-plan/by-date")
    public ResponseEntity<TodayReadingResponse> getByDate(
            @PathVariable UUID departmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal String memberId) {
        DepartmentId deptId = DepartmentId.from(departmentId);
        ReadingPlanDay day = readingPlanQueryUseCase.getReadingByDate(deptId, date);
        if (day == null) return ResponseEntity.noContent().build();

        String planTitle = readingPlanQueryUseCase.getActivePlanTitle(deptId);

        boolean completed = false;
        try {
            completed = readingPlanQueryUseCase.getMyProgress(
                    deptId, MemberId.from(UUID.fromString(memberId)))
                    .completedDates().contains(date);
        } catch (Exception ignored) {}

        return ResponseEntity.ok(TodayReadingResponse.from(day, completed, planTitle));
    }

    @Operation(summary = "전체 일자 목록 조회 (캘린더용)")
    @GetMapping("/departments/{departmentId}/reading-plan/days")
    public ResponseEntity<List<ReadingPlanDayResponse>> getAllDays(@PathVariable UUID departmentId) {
        List<ReadingPlanDay> days = readingPlanQueryUseCase.getAllDays(DepartmentId.from(departmentId));
        return ResponseEntity.ok(ReadingPlanDayResponse.from(days));
    }

    @Operation(summary = "내 진도 조회")
    @GetMapping("/departments/{departmentId}/reading-plan/me/progress")
    public ResponseEntity<MyReadingProgressResponse> getMyProgress(
            @PathVariable UUID departmentId,
            @AuthenticationPrincipal String memberId) {
        ReadingPlanQueryUseCase.MyReadingProgress progress = readingPlanQueryUseCase.getMyProgress(
                DepartmentId.from(departmentId), MemberId.from(UUID.fromString(memberId)));
        return ResponseEntity.ok(MyReadingProgressResponse.from(progress));
    }

    @Operation(summary = "부서 전체 진도 조회 (리더/관리자)")
    @GetMapping("/departments/{departmentId}/reading-plan/progress")
    @RequireDepartmentRole(DepartmentRole.LEADER)
    public ResponseEntity<List<DepartmentReadingProgressResponse>> getDepartmentProgress(
            @PathVariable UUID departmentId) {
        List<ReadingPlanQueryUseCase.MemberReadingProgress> progressList =
                readingPlanQueryUseCase.getDepartmentProgress(DepartmentId.from(departmentId));
        return ResponseEntity.ok(DepartmentReadingProgressResponse.from(progressList));
    }

    // ── 완독 체크 ─────────────────────────────────────────────────────────────

    @Operation(summary = "완독 체크")
    @PostMapping("/departments/{departmentId}/reading-plan-days/{dayId}/completion")
    public ResponseEntity<Void> markComplete(
            @PathVariable UUID departmentId,
            @PathVariable UUID dayId,
            @AuthenticationPrincipal String memberId) {
        readingCompletionCommandUseCase.markComplete(
                DepartmentId.from(departmentId),
                ReadingPlanDayId.from(dayId),
                MemberId.from(UUID.fromString(memberId)));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "완독 취소")
    @DeleteMapping("/departments/{departmentId}/reading-plan-days/{dayId}/completion")
    public ResponseEntity<Void> unmarkComplete(
            @PathVariable UUID departmentId,
            @PathVariable UUID dayId,
            @AuthenticationPrincipal String memberId) {
        readingCompletionCommandUseCase.unmarkComplete(
                DepartmentId.from(departmentId),
                ReadingPlanDayId.from(dayId),
                MemberId.from(UUID.fromString(memberId)));
        return ResponseEntity.noContent().build();
    }

    // ── 관리자: 플랜 생성 및 활성화 ───────────────────────────────────────────

    @Operation(summary = "플랜 생성 (관리자)")
    @PostMapping("/reading-plans")
    public ResponseEntity<Void> createPlan(@RequestBody @Valid CreateReadingPlanRequest request) {
        readingPlanCommandUseCase.createPlan(request.churchId(), request.title(), request.readingDays());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "일자별 분량 일괄 등록 (관리자)")
    @PostMapping("/reading-plans/{planId}/days/batch")
    public ResponseEntity<Void> createDaysBatch(
            @PathVariable UUID planId,
            @RequestBody @Valid List<CreateReadingPlanDayRequest> requests) {
        List<ReadingPlanCommandUseCase.DayInput> inputs = requests.stream()
                .map(r -> new ReadingPlanCommandUseCase.DayInput(
                        r.dayNumber(), r.readingRange(),
                        r.audioUrl(), r.videoUrl(), r.description()))
                .toList();
        readingPlanCommandUseCase.createDaysBatch(planId, inputs);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "부서에 플랜 활성화 (관리자)")
    @PostMapping("/departments/{departmentId}/reading-plan/activate")
    @RequireDepartmentRole(DepartmentRole.ADMIN)
    public ResponseEntity<Void> activatePlan(
            @PathVariable UUID departmentId,
            @RequestBody @Valid ActivateReadingPlanRequest request) {
        readingPlanCommandUseCase.activatePlanForDepartment(
                DepartmentId.from(departmentId), request.readingPlanId(),
                request.startDate(), request.endDate());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "부서 플랜 비활성화 (관리자)")
    @DeleteMapping("/departments/{departmentId}/reading-plan/activate")
    @RequireDepartmentRole(DepartmentRole.ADMIN)
    public ResponseEntity<Void> deactivatePlan(@PathVariable UUID departmentId) {
        readingPlanCommandUseCase.deactivatePlanForDepartment(DepartmentId.from(departmentId));
        return ResponseEntity.noContent().build();
    }
}

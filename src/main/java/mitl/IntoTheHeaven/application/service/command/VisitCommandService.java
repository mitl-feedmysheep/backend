package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.VisitCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.AddVisitMembersCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.CreateVisitCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateVisitCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateVisitMemberCommand;
import mitl.IntoTheHeaven.application.port.out.ChurchPort;
import mitl.IntoTheHeaven.application.port.out.VisitPort;
import mitl.IntoTheHeaven.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VisitCommandService implements VisitCommandUseCase {

        private final VisitPort visitPort;
        private final ChurchPort churchPort;

        // ADMIN - Create a new visit
        @Override
        public Visit createVisit(CreateVisitCommand command) {
                // Retrieve pastor member
                ChurchMember pastorMember = churchPort.findChurchMemberByMemberIdAndChurchId(command.pastorMemberId(),
                                command.churchId());

                // Create Visit
                Visit visit = Visit.builder()
                                .id(VisitId.from(UUID.randomUUID()))
                                .churchId(command.churchId())
                                .pastorMemberId(pastorMember.getId())
                                .date(command.date())
                                .startedAt(command.startedAt())
                                .endedAt(command.endedAt())
                                .place(command.place())
                                .expense(command.expense())
                                .notes(command.notes())
                                .build();

                return visitPort.save(visit);
        }

        // ADMIN - Update visit
        @Override
        public Visit updateVisit(VisitId visitId, UpdateVisitCommand command) {
                Visit existingVisit = visitPort.findDetailById(visitId)
                                .orElseThrow(() -> new IllegalArgumentException("Visit not found: " + visitId));

                // Update Visit basic information only
                Visit updatedVisit = existingVisit.toBuilder()
                                .date(command.date())
                                .startedAt(command.startedAt())
                                .endedAt(command.endedAt())
                                .place(command.place())
                                .expense(command.expense())
                                .notes(command.notes())
                                .build();

                return visitPort.save(updatedVisit);
        }

        // ADMIN - Delete visit (soft delete)
        @Override
        public void deleteVisit(VisitId visitId) {
                Visit visit = visitPort.findDetailById(visitId)
                                .orElseThrow(() -> new IllegalArgumentException("Visit not found: " + visitId));
                visitPort.delete(visit);
        }

        // ADMIN - Add members to visit
        @Override
        public Visit addMembersToVisit(VisitId visitId, AddVisitMembersCommand command) {
                Visit visit = visitPort.findDetailById(visitId)
                                .orElseThrow(() -> new IllegalArgumentException("Visit not found: " + visitId));

                // Find ChurchMembers and validate
                List<ChurchMember> churchMembers = command.memberIds().stream()
                                .map(memberId -> {
                                        ChurchMember churchMember = churchPort.findChurchMemberByMemberIdAndChurchId(
                                                        memberId, visit.getChurchId());
                                        if (churchMember == null) {
                                                throw new IllegalArgumentException(
                                                                "Church member not found for memberId: "
                                                                                + memberId.getValue()
                                                                                + " and churchId: "
                                                                                + visit.getChurchId().getValue());
                                        }
                                        return churchMember;
                                })
                                .collect(Collectors.toList());

                // Check for duplicate members (already added to this visit)
                Set<ChurchMemberId> existingChurchMemberIds = visit.getVisitMembers().stream()
                                .map(VisitMember::getChurchMemberId)
                                .collect(Collectors.toSet());

                List<ChurchMemberId> duplicateChurchMembers = churchMembers.stream()
                                .filter(cm -> existingChurchMemberIds.contains(cm.getId()))
                                .map(ChurchMember::getId)
                                .collect(Collectors.toList());

                if (!duplicateChurchMembers.isEmpty()) {
                        throw new IllegalArgumentException(
                                        "Some members are already added to this visit: " + duplicateChurchMembers);
                }

                // Create new VisitMembers without story and prayers
                List<VisitMember> newVisitMembers = churchMembers.stream()
                                .map(churchMember -> VisitMember.builder()
                                                .id(VisitMemberId.from(UUID.randomUUID()))
                                                .visitId(visitId)
                                                .churchMemberId(churchMember.getId())
                                                .story(null)
                                                .prayers(new ArrayList<>())
                                                .build())
                                .collect(Collectors.toList());

                // Add new members to existing visit members
                List<VisitMember> allVisitMembers = new ArrayList<>(visit.getVisitMembers());
                allVisitMembers.addAll(newVisitMembers);

                Visit updatedVisit = visit.toBuilder()
                                .visitMembers(allVisitMembers)
                                .build();

                return visitPort.save(updatedVisit);
        }

        // ADMIN - Remove member from visit (soft delete)
        @Override
        public Visit removeMemberFromVisit(VisitId visitId, VisitMemberId visitMemberId) {
                Visit visit = visitPort.findDetailById(visitId)
                                .orElseThrow(() -> new IllegalArgumentException("Visit not found: " + visitId));

                // Soft delete the visit member by setting deletedAt
                List<VisitMember> updatedVisitMembers = visit.getVisitMembers().stream()
                                .map(vm -> vm.getId().equals(visitMemberId) ? vm.delete() : vm)
                                .collect(Collectors.toList());

                Visit updatedVisit = visit.toBuilder()
                                .visitMembers(updatedVisitMembers)
                                .build();

                return visitPort.save(updatedVisit);
        }

        // ADMIN - Update visit member story and prayers
        @Override
        public VisitMember updateVisitMember(UpdateVisitMemberCommand command) {
                // 1. Retrieve existing visit with detailed information
                Visit existingVisit = visitPort.findDetailById(command.getVisitId())
                                .orElseThrow(() -> new IllegalArgumentException("Visit not found"));

                // 2. Find VisitMember to update
                VisitMember targetVisitMember = existingVisit.getVisitMembers().stream()
                                .filter(vm -> vm.getId().equals(command.getVisitMemberId()))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("Visit member not found"));

                // 3. Build upserted prayer list (full sync with soft delete)
                Map<UUID, Prayer> existingPrayerById = targetVisitMember.getPrayers().stream()
                                .collect(Collectors.toMap(p -> p.getId().getValue(), Function.identity()));

                List<Prayer> mergedPrayers = new ArrayList<>();

                // Process prayers from request (update or create)
                for (UpdateVisitMemberCommand.PrayerUpdateCommand pCmd : command.getPrayers()) {
                        if (pCmd.getId() != null && existingPrayerById.containsKey(pCmd.getId())) {
                                // Update existing: preserve id and isAnswered
                                Prayer prev = existingPrayerById.get(pCmd.getId());
                                Prayer updated = Prayer.builder()
                                                .id(prev.getId())
                                                .member(targetVisitMember.getChurchMember().getMember())
                                                .memberId(targetVisitMember.getChurchMember().getMemberId())
                                                .visitMember(targetVisitMember)
                                                .visitMemberId(targetVisitMember.getId())
                                                .prayerRequest(pCmd.getPrayerRequest())
                                                .description(pCmd.getDescription())
                                                .isAnswered(prev.isAnswered())
                                                .createdAt(prev.getCreatedAt())
                                                .build();
                                mergedPrayers.add(updated);
                        } else {
                                // Create new
                                Prayer created = Prayer.builder()
                                                .id(PrayerId.from(UUID.randomUUID()))
                                                .member(targetVisitMember.getChurchMember().getMember())
                                                .memberId(targetVisitMember.getChurchMember().getMemberId())
                                                .visitMember(targetVisitMember)
                                                .visitMemberId(targetVisitMember.getId())
                                                .prayerRequest(pCmd.getPrayerRequest())
                                                .description(pCmd.getDescription())
                                                .isAnswered(false)
                                                .build();
                                mergedPrayers.add(created);
                        }
                }

                // Soft delete prayers not in request (set deletedAt)
                for (Prayer existingPrayer : targetVisitMember.getPrayers()) {
                        boolean isInRequest = command.getPrayers().stream()
                                        .anyMatch(pCmd -> pCmd.getId() != null &&
                                                        pCmd.getId().equals(existingPrayer.getId().getValue()));

                        if (!isInRequest) {
                                // Soft delete: set deletedAt
                                Prayer deletedPrayer = existingPrayer.delete();
                                mergedPrayers.add(deletedPrayer);
                        }
                }

                // 4. Create updated VisitMember (includes soft-deleted prayers)
                VisitMember updatedVisitMember = VisitMember.builder()
                                .id(targetVisitMember.getId())
                                .visitId(targetVisitMember.getVisitId())
                                .churchMemberId(targetVisitMember.getChurchMemberId())
                                .churchMember(targetVisitMember.getChurchMember())
                                .story(command.getStory())
                                .prayers(mergedPrayers)
                                .build();

                // 5. Replace only the target member in the VisitMember list
                List<VisitMember> updatedVisitMembers = existingVisit.getVisitMembers().stream()
                                .map(vm -> vm.getId().equals(command.getVisitMemberId())
                                                ? updatedVisitMember
                                                : vm)
                                .collect(Collectors.toList());

                // 6. Create and save updated visit
                Visit updatedVisit = existingVisit.toBuilder()
                                .visitMembers(updatedVisitMembers)
                                .build();

                visitPort.save(updatedVisit);

                return updatedVisitMember;
        }
}

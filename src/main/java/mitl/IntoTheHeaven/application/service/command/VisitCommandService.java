package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.VisitCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.AddVisitMembersCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.CreateVisitCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateVisitCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.VisitMemberCommand;
import mitl.IntoTheHeaven.application.port.out.ChurchPort;
import mitl.IntoTheHeaven.application.port.out.VisitPort;
import mitl.IntoTheHeaven.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
                Visit existingVisit = visitPort.findById(visitId)
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
                Visit visit = visitPort.findById(visitId)
                                .orElseThrow(() -> new IllegalArgumentException("Visit not found: " + visitId));
                visitPort.delete(visit);
        }

        // ADMIN - Add members to visit
        @Override
        public Visit addMembersToVisit(VisitId visitId, AddVisitMembersCommand command) {
                Visit visit = visitPort.findById(visitId)
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
                Visit visit = visitPort.findById(visitId)
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
}

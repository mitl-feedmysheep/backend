package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.VisitCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.CreateVisitCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateVisitCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.VisitMemberCommand;
import mitl.IntoTheHeaven.application.port.out.VisitPort;
import mitl.IntoTheHeaven.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VisitCommandService implements VisitCommandUseCase {

    private final VisitPort visitPort;

    // ADMIN - Create a new visit
    @Override
    public Visit createVisit(CreateVisitCommand command) {
        // Generate IDs
        VisitId visitId = VisitId.from(UUID.randomUUID());

        // Create VisitMembers
        List<VisitMember> visitMembers = command.visitMembers().stream()
                .map(vmCmd -> createVisitMember(vmCmd, visitId))
                .collect(Collectors.toList());

        // Create Visit
        Visit visit = Visit.builder()
                .id(visitId)
                .churchId(command.churchId())
                .pastorChurchMemberId(command.pastorChurchMemberId())
                .date(command.date())
                .startedAt(command.startedAt())
                .endedAt(command.endedAt())
                .place(command.place())
                .expense(command.expense())
                .notes(command.notes())
                .visitMembers(visitMembers)
                .build();

        return visitPort.save(visit);
    }

    // ADMIN - Update visit
    @Override
    public Visit updateVisit(VisitId visitId, UpdateVisitCommand command) {
        Visit existingVisit = visitPort.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found: " + visitId));

        // Create updated VisitMembers
        List<VisitMember> updatedVisitMembers = command.visitMembers().stream()
                .map(vmCmd -> createVisitMember(vmCmd, visitId))
                .collect(Collectors.toList());

        // Update Visit
        Visit updatedVisit = existingVisit.toBuilder()
                .date(command.date())
                .startedAt(command.startedAt())
                .endedAt(command.endedAt())
                .place(command.place())
                .expense(command.expense())
                .notes(command.notes())
                .visitMembers(updatedVisitMembers)
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

    /**
     * Create VisitMember from command
     */
    private VisitMember createVisitMember(VisitMemberCommand command, VisitId visitId) {
        VisitMemberId visitMemberId = VisitMemberId.from(UUID.randomUUID());

        // Create Prayers
        List<Prayer> prayers = command.prayers().stream()
                .map(prayerCmd -> Prayer.builder()
                        .id(PrayerId.from(UUID.randomUUID()))
                        .visitMemberId(visitMemberId)
                        .prayerRequest(prayerCmd.prayerRequest())
                        .description(prayerCmd.description())
                        .isAnswered(false)
                        .build())
                .collect(Collectors.toList());

        return VisitMember.builder()
                .id(visitMemberId)
                .visitId(visitId)
                .churchMemberId(command.churchMemberId())
                .story(command.story())
                .prayers(prayers)
                .build();
    }
}


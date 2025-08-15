package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.AggregateRoot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@SuperBuilder(toBuilder = true)
public class Gathering extends AggregateRoot<Gathering, GatheringId> {

    private final Group group;
    private final String name;
    private final String description;
    private final LocalDate date;
    private final LocalDateTime startedAt;
    private final LocalDateTime endedAt;
    private final String place;
    private final String leaderComment;
    private final String adminComment;
    private final List<GatheringMember> gatheringMembers;

    public Gathering addGatheringMembers(List<GatheringMember> members) {
        List<GatheringMember> newMembers = new ArrayList<>(this.gatheringMembers);
        newMembers.addAll(members);
        
        return this.toBuilder()
                .gatheringMembers(newMembers)
                .build();
    }
} 
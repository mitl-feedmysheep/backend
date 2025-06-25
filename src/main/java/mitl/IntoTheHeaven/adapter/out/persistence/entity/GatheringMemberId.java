package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GatheringMemberId implements Serializable {
    private UUID gathering;
    private UUID groupMember;
} 
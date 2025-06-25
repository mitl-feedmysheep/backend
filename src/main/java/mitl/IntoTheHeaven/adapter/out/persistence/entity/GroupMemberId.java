package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GroupMemberId implements Serializable {
    private UUID group;
    private UUID member;
} 
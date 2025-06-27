package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.MemberJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.GroupPersistenceMapper;
import mitl.IntoTheHeaven.application.port.out.GroupPort;
import mitl.IntoTheHeaven.domain.model.Group;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GroupPersistenceAdapter implements GroupPort {

    private final MemberJpaRepository memberJpaRepository;
    private final GroupPersistenceMapper groupPersistenceMapper;

    @Override
    public List<Group> findGroupsByMemberId(UUID memberId) {
        return memberJpaRepository.findWithGroupsById(memberId)
                .map(member -> member.getGroupMembers().stream()
                        .map(groupMember -> groupPersistenceMapper.toDomain(groupMember.getGroup()))
                        .toList())
                .orElse(Collections.emptyList());
    }
} 
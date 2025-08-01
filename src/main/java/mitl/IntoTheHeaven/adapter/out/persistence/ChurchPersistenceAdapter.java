package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.MemberJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.ChurchPersistenceMapper;
import mitl.IntoTheHeaven.application.port.out.ChurchPort;
import mitl.IntoTheHeaven.domain.model.Church;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChurchPersistenceAdapter implements ChurchPort {

    private final MemberJpaRepository memberJpaRepository;
    private final ChurchPersistenceMapper churchPersistenceMapper;

    @Override
    public List<Church> findChurchesByMemberId(UUID memberId) {
        return memberJpaRepository.findWithChurchesById(memberId)
                .map(member -> member.getChurchMembers().stream()
                        .map(churchMember -> churchMember.getChurch())
                        .map(churchPersistenceMapper::toDomain)
                        .toList())
                .orElse(Collections.emptyList());
    }
}
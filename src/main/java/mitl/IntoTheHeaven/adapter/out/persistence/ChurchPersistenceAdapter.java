package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.ChurchJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.ChurchMemberJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.ChurchPersistenceMapper;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.ChurchMemberPersistenceMapper;
import mitl.IntoTheHeaven.application.port.out.ChurchPort;
import mitl.IntoTheHeaven.domain.enums.ChurchRole;
import mitl.IntoTheHeaven.domain.model.Church;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMember;
import mitl.IntoTheHeaven.domain.model.MemberId;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChurchPersistenceAdapter implements ChurchPort {

    private final ChurchJpaRepository churchJpaRepository;
    private final ChurchMemberJpaRepository churchMemberJpaRepository;
    private final ChurchPersistenceMapper churchPersistenceMapper;
    private final ChurchMemberPersistenceMapper churchMemberPersistenceMapper;

    @Override
    public List<Church> findChurchesByMemberId(UUID memberId) {
        return churchMemberJpaRepository.findAllByMemberId(memberId)
                .stream()
                .map(churchMember -> churchPersistenceMapper.toDomain(churchMember.getChurch()))
                .toList();
    }

    @Override
    public Church findById(UUID churchId) {
        return churchPersistenceMapper.toDomain(churchJpaRepository.findById(churchId)
                .orElse(null));
    }

    @Override
    public List<MemberId> findMemberIdsByChurchId(UUID churchId) {
        return churchMemberJpaRepository.findAllByChurchId(churchId)
                .stream()
                .map(churchMember -> MemberId.from(churchMember.getMember().getId()))
                .toList();
    }

    @Override
    public ChurchMember findChurchMemberByMemberIdAndChurchId(MemberId memberId, ChurchId churchId) {
        return churchMemberPersistenceMapper.toDomain(
                churchMemberJpaRepository.findByMemberIdAndChurchId(memberId.getValue(), churchId.getValue()));
    }

    @Override
    public List<ChurchMember> findChurchMembersByMemberIdAndRole(MemberId memberId, ChurchRole role) {
        return churchMemberJpaRepository.findAllByMemberIdAndRole(memberId.getValue(), role)
                .stream()
                .map(churchMember -> churchMemberPersistenceMapper.toDomain(churchMember))
                .toList();
    }
}
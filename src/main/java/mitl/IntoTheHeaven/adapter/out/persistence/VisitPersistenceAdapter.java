package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.VisitPersistenceMapper;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.VisitJpaRepository;
import mitl.IntoTheHeaven.application.port.out.VisitPort;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMemberId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Visit;
import mitl.IntoTheHeaven.domain.model.VisitId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VisitPersistenceAdapter implements VisitPort {

    private final VisitJpaRepository visitJpaRepository;
    private final VisitPersistenceMapper visitPersistenceMapper;

    @Override
    public Visit save(Visit visit) {
        var entity = visitPersistenceMapper.toEntity(visit, visit.getChurchId().getValue());
        var savedEntity = visitJpaRepository.save(entity);
        return visitPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Visit> findById(VisitId visitId) {
        return visitJpaRepository.findWithDetailsById(visitId.getValue())
                .map(visitPersistenceMapper::toDomain);
    }

    @Override
    public List<Visit> findAllByChurchIdAndMemberId(ChurchId churchId, MemberId memberId) {
        return visitJpaRepository
                .findAllByChurchIdAndMemberIdOrderByDateDescStartedAtDesc(churchId.getValue(), memberId.getValue())
                .stream()
                .map(visitPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Visit visit) {
        Visit deletedVisit = visit.delete();
        save(deletedVisit);
    }

    @Override
    public List<Visit> findMyVisits(ChurchMemberId churchMemberId) {
        return visitJpaRepository.findMyVisits(churchMemberId.getValue())
                .stream()
                .map(visitPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }
}

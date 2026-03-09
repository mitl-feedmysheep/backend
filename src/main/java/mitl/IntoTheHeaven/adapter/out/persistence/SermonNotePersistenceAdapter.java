package mitl.IntoTheHeaven.adapter.out.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.SermonNoteJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.MemberJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.SermonNoteJpaRepository;
import mitl.IntoTheHeaven.application.port.out.SermonNotePort;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.SermonNote;
import mitl.IntoTheHeaven.domain.model.SermonNoteId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static mitl.IntoTheHeaven.adapter.out.persistence.entity.QSermonNoteJpaEntity.sermonNoteJpaEntity;

@Component
@RequiredArgsConstructor
public class SermonNotePersistenceAdapter implements SermonNotePort {

    private final SermonNoteJpaRepository sermonNoteJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<SermonNote> findAllByMemberId(UUID memberId) {
        return sermonNoteJpaRepository.findAllByMemberIdOrderBySermonDateDesc(memberId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SermonNote> findById(UUID sermonNoteId) {
        return sermonNoteJpaRepository.findById(sermonNoteId).map(this::toDomain);
    }

    @Override
    public SermonNote save(SermonNote sermonNote) {
        SermonNoteJpaEntity entity = toEntity(sermonNote);
        SermonNoteJpaEntity saved = sermonNoteJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<String> findDistinctServiceTypesByMemberId(UUID memberId) {
        return queryFactory
                .selectDistinct(sermonNoteJpaEntity.serviceType)
                .from(sermonNoteJpaEntity)
                .where(
                        sermonNoteJpaEntity.member.id.eq(memberId)
                                .and(sermonNoteJpaEntity.serviceType.isNotNull())
                                .and(sermonNoteJpaEntity.serviceType.ne(""))
                )
                .orderBy(sermonNoteJpaEntity.serviceType.asc())
                .fetch();
    }

    private SermonNote toDomain(SermonNoteJpaEntity entity) {
        return SermonNote.builder()
                .id(SermonNoteId.from(entity.getId()))
                .memberId(MemberId.from(entity.getMember().getId()))
                .title(entity.getTitle())
                .sermonDate(entity.getSermonDate())
                .preacher(entity.getPreacher())
                .serviceType(entity.getServiceType())
                .scripture(entity.getScripture())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private SermonNoteJpaEntity toEntity(SermonNote sermonNote) {
        return SermonNoteJpaEntity.builder()
                .id(sermonNote.getId().getValue())
                .title(sermonNote.getTitle())
                .sermonDate(sermonNote.getSermonDate())
                .preacher(sermonNote.getPreacher())
                .serviceType(sermonNote.getServiceType())
                .scripture(sermonNote.getScripture())
                .content(sermonNote.getContent())
                .member(memberJpaRepository.getReferenceById(sermonNote.getMemberId().getValue()))
                .deletedAt(sermonNote.getDeletedAt())
                .build();
    }
}

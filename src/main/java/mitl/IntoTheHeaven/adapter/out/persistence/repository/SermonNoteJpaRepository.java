package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.SermonNoteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SermonNoteJpaRepository extends JpaRepository<SermonNoteJpaEntity, UUID> {

    List<SermonNoteJpaEntity> findAllByMemberIdOrderBySermonDateDesc(UUID memberId);
}

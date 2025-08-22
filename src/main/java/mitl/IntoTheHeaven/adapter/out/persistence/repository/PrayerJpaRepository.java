package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.PrayerJpaEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrayerJpaRepository extends JpaRepository<PrayerJpaEntity, UUID> {

    List<PrayerJpaEntity> findAllByMemberIdIn(List<UUID> memberIds);
}

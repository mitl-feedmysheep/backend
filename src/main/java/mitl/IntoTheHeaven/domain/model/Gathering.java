package mitl.IntoTheHeaven.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.domain.enums.MediaType;
import mitl.IntoTheHeaven.global.domain.AggregateRoot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Builder.Default
    private final List<Media> medias = new ArrayList<>();

    /**
     * Thumbnail URL for list views (actual thumbnail image)
     */
    public Optional<String> getThumbnailUrl() {
        return medias.stream()
                .filter(media -> media.getMediaType() == MediaType.THUMBNAIL)
                .findFirst()
                .map(Media::getUrl);
    }

    /**
     * Main photo URLs for gallery (500x500, registration order)
     */
    public List<String> getAllPhotoUrls() {
        return medias.stream()
                .filter(media -> media.getMediaType() == MediaType.MEDIUM)
                .sorted((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt()))
                .map(Media::getUrl)
                .toList();
    }

    /**
     * Get main image URL (first medium image as representative)
     */
    public Optional<String> getMainImageUrl() {
        return medias.stream()
                .filter(media -> media.getMediaType() == MediaType.MEDIUM)
                .sorted((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt()))
                .findFirst()
                .map(Media::getUrl);
    }

    /**
     * Check if gathering has photos
     */
    public boolean hasPhotos() {
        return medias.stream()
                .anyMatch(media -> media.getMediaType() == MediaType.MEDIUM);
    }

    /**
     * Get photo count (medium size images)
     */
    public int getPhotoCount() {
        return (int) medias.stream()
                .filter(media -> media.getMediaType() == MediaType.MEDIUM)
                .count();
    }

    /**
     * 미디어 보유 여부
     */
    public boolean hasMedia() {
        return !medias.isEmpty();
    }

    public Gathering addGatheringMembers(List<GatheringMember> members) {
        List<GatheringMember> newMembers = new ArrayList<>(this.gatheringMembers);
        newMembers.addAll(members);

        return this.toBuilder()
                .gatheringMembers(newMembers)
                .build();
    }
}
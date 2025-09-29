package mitl.IntoTheHeaven.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.domain.enums.MediaType;
import mitl.IntoTheHeaven.global.domain.AggregateRoot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@SuperBuilder
public class Group extends AggregateRoot<Group, GroupId> {

    private final String name;
    private final String description;
    private final ChurchId churchId;
    private final LocalDate startDate;
    private final LocalDate endDate;

    @Builder.Default
    private final List<Media> medias = new ArrayList<>();

    /**
     * Thumbnail URL for list views (200x200)
     */
    public Optional<String> getThumbnailUrl() {
        return medias.stream()
                .filter(media -> media.getMediaType() == MediaType.THUMBNAIL)
                .findFirst()
                .map(Media::getUrl);
    }

    /**
     * Main image URL for detail views (500x500)
     */
    public Optional<String> getMainImageUrl() {
        return medias.stream()
                .filter(media -> media.getMediaType() == MediaType.MEDIUM)
                .findFirst()
                .map(Media::getUrl);
    }

    /**
     * 미디어 보유 여부
     */
    public boolean hasMedia() {
        return !medias.isEmpty();
    }

    /**
     * 썸네일 보유 여부
     */
    public boolean hasThumbnail() {
        return getThumbnailUrl().isPresent();
    }
}
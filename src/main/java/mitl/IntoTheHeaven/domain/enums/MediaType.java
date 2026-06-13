package mitl.IntoTheHeaven.domain.enums;

/**
 * Media types for optimized storage and performance
 */
public enum MediaType {
    THUMBNAIL,    // 200x200 - for list views, profile icons
    MEDIUM,       // 500x500 - for detail views, main images
    ORIGINAL      // 원본 그대로 (어드민 직접 업로드)
}

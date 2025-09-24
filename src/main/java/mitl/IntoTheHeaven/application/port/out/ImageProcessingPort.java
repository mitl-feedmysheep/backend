package mitl.IntoTheHeaven.application.port.out;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Image Processing (Resizing) Port
 */
public interface ImageProcessingPort {

    // Record for image size (Java 14+)
    record ImageSize(int width, int height) {
        // Predefined common sizes
        public static final ImageSize THUMBNAIL = new ImageSize(200, 200);
        public static final ImageSize SMALL = new ImageSize(400, 400);
        public static final ImageSize MEDIUM = new ImageSize(800, 800);
        public static final ImageSize LARGE = new ImageSize(1200, 1200);
    }

    // Record for resized image result
    record ResizedImage(byte[] data, String fileName, String contentType) {}

    /**
     * Resize original image to multiple sizes
     * 
     * @param originalFile Original image file
     * @param baseFileName Base file name (without extension)
     * @return Map<Size name, Resized image>
     */
    Map<String, ResizedImage> resizeToMultipleSizes(MultipartFile originalFile, String baseFileName);

    /**
     * Resize to single size
     * 
     * @param originalFile Original image file
     * @param size Target size
     * @param fileName File name
     * @return Resized image
     */
    ResizedImage resizeToSingleSize(MultipartFile originalFile, ImageSize size, String fileName);

    /**
     * Check if file is an image
     */
    boolean isImageFile(MultipartFile file);
}
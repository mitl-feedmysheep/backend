package mitl.IntoTheHeaven.adapter.out.storage;

import lombok.extern.slf4j.Slf4j;
import mitl.IntoTheHeaven.application.port.out.ImageProcessingPort;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class ThumbnailatorImageProcessingAdapter implements ImageProcessingPort {

    // Supported image MIME types
    private static final String[] SUPPORTED_IMAGE_TYPES = {
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/bmp", "image/webp",
            "image/heic", "image/heif" // iPhone HEIC format support
    };

    @Override
    public Map<String, ResizedImage> resizeToMultipleSizes(MultipartFile originalFile, String baseFileName) {
        Map<String, ResizedImage> resizedImages = new LinkedHashMap<>();

        try {
            // Save original image as is
            resizedImages.put("ORIGINAL", new ResizedImage(
                    originalFile.getBytes(),
                    baseFileName + "_ORIGINAL." + getFileExtension(originalFile),
                    originalFile.getContentType()));

            // Create thumbnail (200x200)
            ResizedImage thumbnail = resizeToSingleSize(originalFile, ImageSize.THUMBNAIL,
                    baseFileName + "_THUMBNAIL." + getFileExtension(originalFile));
            resizedImages.put("THUMBNAIL", thumbnail);

            // Create small size (400x400)
            ResizedImage small = resizeToSingleSize(originalFile, ImageSize.SMALL,
                    baseFileName + "_RESIZED_SMALL." + getFileExtension(originalFile));
            resizedImages.put("RESIZED_SMALL", small);

            // Create medium size (800x800) - optional (temporarily disabled)
            // ResizedImage medium = resizeToSingleSize(originalFile, ImageSize.MEDIUM, 
            //     baseFileName + "_RESIZED_MEDIUM." + getFileExtension(originalFile));
            // resizedImages.put("RESIZED_MEDIUM", medium);

            log.info("Successfully resized image to {} different sizes: {}",
                    resizedImages.size(), baseFileName);

        } catch (IOException e) {
            log.error("Failed to resize image: {}", baseFileName, e);
            throw new RuntimeException("Failed to resize image: " + e.getMessage(), e);
        }

        return resizedImages;
    }

    @Override
    public ResizedImage resizeToSingleSize(MultipartFile originalFile, ImageSize size, String fileName) {
        try {
            // Resizing using Thumbnailator
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Thumbnails.of(originalFile.getInputStream())
                    .size(size.width(), size.height()) // Record accessor method
                    .keepAspectRatio(true) // Keep aspect ratio
                    .outputQuality(0.85) // Quality 85%
                    .toOutputStream(outputStream);

            byte[] resizedData = outputStream.toByteArray();

            log.debug("Resized image: {} -> {}x{}, size: {} bytes",
                    fileName, size.width(), size.height(), resizedData.length); // Record accessor methods

            return new ResizedImage(resizedData, fileName, originalFile.getContentType());

        } catch (IOException e) {
            log.error("Failed to resize image to size {}x{}: {}",
                    size.width(), size.height(), fileName, e); // Record accessor methods
            throw new RuntimeException("Failed to resize image: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }

        for (String supportedType : SUPPORTED_IMAGE_TYPES) {
            if (contentType.toLowerCase().startsWith(supportedType)) {
                return true;
            }
        }

        log.warn("Unsupported file type: {}, file: {}", contentType, file.getOriginalFilename());
        return false;
    }

    /**
     * Extract file extension (without dot)
     */
    private String getFileExtension(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || !originalFileName.contains(".")) {
            return "jpg"; // Default value
        }
        return originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
    }
}
package mitl.IntoTheHeaven.application.port.out;

import java.time.Duration;

/**
 * Port for interacting with cloud storage services (S3-compatible)
 * Provides abstraction for cloud storage operations regardless of provider
 */
public interface CloudPort {

    /**
     * Generates a presigned URL for uploading an object to cloud storage.
     *
     * @param bucketName    The name of the storage bucket.
     * @param objectKey     The full path/key of the object in the bucket.
     * @param contentType   The content type of the object (e.g., "image/jpeg").
     * @param contentLength The expected size of the object in bytes.
     * @param expiration    The duration for which the presigned URL is valid.
     * @return A presigned URL string.
     */
    String generatePresignedUploadUrl(String bucketName, String objectKey,
            String contentType, Long contentLength, Duration expiration);

    /**
     * Generates a public URL for accessing an object in cloud storage.
     * This assumes the bucket is configured for public access or a CDN is in front.
     *
     * @param objectKey  The full path/key of the object in the bucket.
     * @return A public URL string.
     */
    String generatePublicUrl(String objectKey);

    /**
     * Checks if a file exists in the storage bucket.
     *
     * @param bucketName The name of the storage bucket.
     * @param objectKey  The full path/key of the object in the bucket.
     * @return True if the file exists, false otherwise.
     */
    boolean fileExists(String bucketName, String objectKey);

    /**
     * Delete file from storage bucket
     *
     * @param bucketName The name of the storage bucket.
     * @param objectKey  The full path/key of the object in the bucket.
     */
    void deleteFile(String bucketName, String objectKey);
}

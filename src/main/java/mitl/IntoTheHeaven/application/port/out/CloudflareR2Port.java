package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.enums.MediaType;

import java.time.Duration;

public interface CloudflareR2Port {

    /**
     * Generate presigned URL for uploading file to R2
     * 
     * @param bucketName  R2 bucket name
     * @param objectKey   File path/key in the bucket
     * @param contentType MIME type of the file
     * @param maxFileSize Maximum allowed file size in bytes
     * @param expiration  Expiration duration for the presigned URL
     * @return Presigned URL for direct upload
     */
    String generatePresignedUploadUrl(String bucketName,
            String objectKey,
            String contentType,
            Long maxFileSize,
            Duration expiration);

    /**
     * Generate public URL for accessing uploaded file
     * 
     * @param bucketName R2 bucket name
     * @param objectKey  File path/key in the bucket
     * @return Public URL for accessing the file
     */
    String generatePublicUrl(String bucketName, String objectKey);

    /**
     * Generate object key (file path) for media upload
     * 
     * @param uploadId  Unique upload identifier
     * @param mediaType Type of media (THUMBNAIL, MEDIUM)
     * @param fileName  Original file name
     * @return Object key for storing in R2
     */
    String generateObjectKey(String uploadId, MediaType mediaType, String fileName);

    /**
     * Generate unique upload ID
     * 
     * @param mediaType Type of media
     * @return Unique upload identifier
     */
    String generateUploadId(MediaType mediaType);

    /**
     * Check if file exists in R2 bucket
     * 
     * @param bucketName R2 bucket name
     * @param objectKey  File path/key in the bucket
     * @return true if file exists, false otherwise
     */
    boolean fileExists(String bucketName, String objectKey);

    /**
     * Delete file from R2 bucket
     * 
     * @param bucketName R2 bucket name
     * @param objectKey  File path/key in the bucket
     */
    void deleteFile(String bucketName, String objectKey);
}

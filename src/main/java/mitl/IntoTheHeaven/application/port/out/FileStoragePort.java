package mitl.IntoTheHeaven.application.port.out;

import org.springframework.web.multipart.MultipartFile;

public interface FileStoragePort {

    /**
     * Save file
     * @param file file to upload
     * @param fileName file name to save (including path)
     * @return path of the saved file
     */
    String saveFile(MultipartFile file, String fileName);

    /**
     * Delete file
     * @param filePath path of the file to delete
     */
    void deleteFile(String filePath);

    /**
     * Check if file exists
     * @param filePath path of the file to check
     * @return whether the file exists
     */
    boolean existsFile(String filePath);
}

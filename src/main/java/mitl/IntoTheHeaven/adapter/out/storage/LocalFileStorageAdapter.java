package mitl.IntoTheHeaven.adapter.out.storage;

import lombok.extern.slf4j.Slf4j;
import mitl.IntoTheHeaven.application.port.out.FileStoragePort;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Component
public class LocalFileStorageAdapter implements FileStoragePort {

    @Value("${app.upload.dir:/app/uploads}")
    private String uploadDir;
    
    @Override
    public String saveFile(MultipartFile file, String fileName) {
        try {
            // Create upload directory
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // File save path (simply saved in the root directory)
            Path filePath = uploadPath.resolve(fileName);

            // Save file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            String savedPath = filePath.toString();
            log.info("File saved successfully: {}", savedPath);
            
            return savedPath;
            
        } catch (IOException e) {
            log.error("Failed to save file: {}", fileName, e);
            throw new RuntimeException("Failed to save file: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("File deleted successfully: {}", filePath);
            } else {
                log.warn("File not found for deletion: {}", filePath);
            }
        } catch (IOException e) {
            log.error("Failed to delete file: {}", filePath, e);
            throw new RuntimeException("Failed to delete file: " + e.getMessage());
        }
    }

    @Override
    public boolean existsFile(String filePath) {
        Path path = Paths.get(filePath);
        boolean exists = Files.exists(path);
        log.debug("File exists check - Path: {}, Exists: {}", filePath, exists);
        return exists;
    }
}

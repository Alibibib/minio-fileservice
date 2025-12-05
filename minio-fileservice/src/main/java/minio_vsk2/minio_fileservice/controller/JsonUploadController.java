package minio_vsk2.minio_fileservice.controller;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/upload/json")
public class JsonUploadController {

    private final MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    public JsonUploadController(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @PostMapping
    public ResponseEntity<String> uploadJsonFile(@RequestParam("file") MultipartFile file) {
        try {
            if (!file.getContentType().equals("application/json")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body("Only JSON files are allowed.");
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(file.getOriginalFilename())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return ResponseEntity.ok("JSON file uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while uploading the file.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }
}
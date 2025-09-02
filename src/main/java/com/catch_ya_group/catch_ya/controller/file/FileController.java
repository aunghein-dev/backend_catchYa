package com.catch_ya_group.catch_ya.controller.file;

import com.catch_ya_group.catch_ya.service.file.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/private/v1/file")
@RequiredArgsConstructor
@Tag(
        name = "Files",
        description = "Endpoints for managing and retrieving user-related files, including uploads, downloads, and metadata."
)
public class FileController {

    private final MinioService minioService;

    @Operation(
            summary = "Upload a file",
            description = "Uploads a file to the storage service. The uploaded file will have a unique timestamp-based name."
    )
    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file) {
        try {
            String fileUrl = minioService.uploadFile(file);
            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Delete a file",
            description = "Deletes a file from the storage service using its URL or object name."
    )
    @DeleteMapping
    public ResponseEntity<String> deleteFile(@RequestParam("fileUrl") String fileUrl) {
        try {
            minioService.deleteFile(fileUrl);
            return ResponseEntity.ok("File deleted successfully: " + fileUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File delete failed: " + e.getMessage());
        }
    }
}

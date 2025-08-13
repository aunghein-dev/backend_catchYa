package com.catch_ya_group.catch_ya.controller.file;

import com.catch_ya_group.catch_ya.service.file.MinioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/public/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Files", description = "API can extract data which are users related data files.")
public class FileController {

    private final MinioService minioService;


    @PostMapping("/file")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String objectName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
            String result = minioService.uploadFile(file, objectName);
            return ResponseEntity.ok("File uploaded successfully: " + result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/{objectName}")
    public ResponseEntity<String> deleteFile(@PathVariable String objectName) {
        try {
            minioService.deleteFile(objectName);
            return ResponseEntity.ok("File deleted successfully: " + objectName);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File delete failed: " + e.getMessage());
        }
    }



}

package com.catch_ya_group.catch_ya.service.file;

import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class MinioService {

    private final MinioClient minioClient;
    private final String bucketName;

    @Value("${minio.url}")
    private String publicBaseUrl;

    public MinioService(MinioClient minioClient,
                        @Value("${minio.bucket-name}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    // Upload file and return public URL
    public String uploadFile(MultipartFile file) throws Exception {
        // Ensure bucket exists
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }

        // 👇 Extract file extension safely
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        // 👇 Only timestamp + extension
        String objectName = System.currentTimeMillis() + extension;

        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }

        // 👇 Ensure proper public URL with /public/
        return publicBaseUrl + "/public/" + objectName;
    }


    // Delete file by objectName or full URL
    public void deleteFile(String filePathOrUrl) throws Exception {
        // If the input is a full URL, extract only the filename
        String objectName = filePathOrUrl;
        if (objectName.startsWith("http")) {
            int lastSlash = objectName.lastIndexOf("/");
            if (lastSlash != -1) {
                objectName = objectName.substring(lastSlash + 1); // 👉 "1756782828701.avif"
            }
        }

        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!bucketExists) {
            throw new IllegalArgumentException("Bucket " + bucketName + " does not exist");
        }

        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("File " + objectName + " does not exist in bucket " + bucketName);
        }

        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
    }

}

package com.catch_ya_group.catch_ya.service.status;

import com.catch_ya_group.catch_ya.modal.dto.StatusCreateRequest;
import com.catch_ya_group.catch_ya.modal.entity.Status;
import com.catch_ya_group.catch_ya.repository.StatusRepository;
import com.catch_ya_group.catch_ya.service.file.MinioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;
    private final MinioService minioService;

    public List<Status> getAll() {
        return statusRepository.findAll();
    }

    public Status getById(Long id) {
        return statusRepository.findById(id).orElse(null);
    }

    public List<Status> getByUserId(Long userId) {
        return statusRepository.findByUserId(userId);
    }

    public List<Status> searchByContent(String keyword) {
        return statusRepository.findByContentContaining(keyword);
    }

    public List<Status> searchByKeyword(String keyword) {
        return statusRepository.findByKeyword(keyword);
    }

    @Transactional
    public Status create(StatusCreateRequest payload, List<MultipartFile> imageFiles) {
        Status status = Status.builder()
                .userId(payload.getUserId())
                .content(payload.getContent())
                .hashKeywords(payload.getHashKeywords() == null ? List.of() : payload.getHashKeywords())
                .images(new java.util.ArrayList<>())
                .build();

        if (imageFiles != null) {
            for (MultipartFile f : imageFiles) {
                if (!f.isEmpty()) {
                    String url = null;
                    try {
                        url = minioService.uploadFile(f);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    status.getImages().add(url);
                }
            }
        }
        return statusRepository.save(status);
    }

    @Transactional
    public Status update(Long id,
                         StatusCreateRequest payload,
                         List<MultipartFile> imageFiles,
                         boolean merge,
                         boolean clearImages) {

        Status existing = statusRepository.findById(id).orElseThrow();

        // keep a copy of current images for diffing & deletion
        List<String> before = existing.getImages() == null
                ? new java.util.ArrayList<>()
                : new java.util.ArrayList<>(existing.getImages());

        // 1) update scalar fields
        existing.setUserId(payload.getUserId());
        existing.setContent(payload.getContent());
        existing.setHashKeywords(payload.getHashKeywords() == null ? List.of() : payload.getHashKeywords());

        // 2) clearing all current images?
        if (clearImages && !before.isEmpty()) {
            for (String url : before) {
                try {
                    minioService.deleteFile(url);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to delete " + url + " from storage", e);
                }
            }
            existing.setImages(new java.util.ArrayList<>());
            // after this point, "before" still has the original values for diffing if needed
        }

        // 3) handle new uploads
        if (imageFiles != null) {
            List<String> uploaded = new java.util.ArrayList<>();
            for (MultipartFile f : imageFiles) {
                if (!f.isEmpty()) {
                    try {
                        String url = minioService.uploadFile(f);
                        uploaded.add(url);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to upload to storage", e);
                    }
                }
            }

            if (merge) {
                // keep existing (possibly cleared above), append new
                List<String> curr = existing.getImages() == null
                        ? new java.util.ArrayList<>()
                        : new java.util.ArrayList<>(existing.getImages());
                curr.addAll(uploaded);
                existing.setImages(curr);
            } else {
                // replace: delete anything that won't survive the replacement
                // (use the state *before* replacement but *after* optional clear)
                List<String> curr = existing.getImages() == null
                        ? new java.util.ArrayList<>()
                        : existing.getImages();

                // compute toRemove = curr - uploaded
                java.util.Set<String> toRemove = new java.util.HashSet<>(curr);
                toRemove.removeAll(uploaded);

                for (String url : toRemove) {
                    try {
                        minioService.deleteFile(url);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to delete " + url + " from storage", e);
                    }
                }
                existing.setImages(uploaded);
            }
        }

        return statusRepository.save(existing);
    }


    public void delete(Long id) {
        statusRepository.deleteById(id);
    }
}

package com.catch_ya_group.catch_ya.service.status;

import com.catch_ya_group.catch_ya.modal.dto.*;
import com.catch_ya_group.catch_ya.modal.entity.Status;
import com.catch_ya_group.catch_ya.repository.StatusRepository;
import com.catch_ya_group.catch_ya.repository.UsersRepository;
import com.catch_ya_group.catch_ya.service.file.MinioService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;
    private final MinioService minioService;
    private final UsersRepository usersRepository;

    private static final Sort NEWEST = Sort.by(Sort.Direction.DESC, "statusDateTime", "statusId");

    public List<Status> getAll() {
        return statusRepository.findAll(NEWEST);
    }

    public Status getById(Long id) {
        return statusRepository.findById(id).orElse(null);
    }

    public List<Status> getByUserId(Long userId) {
        return statusRepository.findByUserIdOrderByStatusDateTimeDescStatusIdDesc(userId);
        // or pageable:
        // return statusRepository.findByUserId(userId, PageRequest.of(0, 50, NEWEST)).getContent();
    }

    public List<Status> searchByContent(String keyword) {
        return statusRepository.findByContentContainingNewest(keyword);
    }

    @Transactional
    public Status create(StatusCreateRequest payload, List<MultipartFile> imageFiles) {
        Status status = Status.builder()
                .userId(payload.getUserId())
                .content(payload.getContent())
                .hashKeywords(payload.getHashKeywords() == null ? List.of() : payload.getHashKeywords())
                .images(new java.util.ArrayList<>())
                .statusDateTime(LocalDateTime.now())
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

    @Transactional
    public void delete(Long id) {
        Status s = statusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Status " + id + " not found"));

        var urls = (s.getImages() == null) ? List.<String>of() : s.getImages();

        for (String url : urls) {
            try {
                minioService.deleteFile(url);
            } catch (Exception e) {
                throw new IllegalStateException("Storage delete failed for " + url, e);
            }
        }
        statusRepository.delete(s);
    }

    public List<ProfilePhotoReponse> getPhotoByUserId(Long userId) {
       return statusRepository.getPhotoByUserId(userId);
    }

    public List<UserWithStatusesResponse> searchByKeyword(String keyword, Double currentUserLat, Double currentUserLong) {
        List<Status> statuses = statusRepository.findByKeywordNewest(keyword);

        List<Long> userIds = statuses.stream()
                .map(Status::getUserId)
                .distinct()
                .collect(Collectors.toList());

        List<Object[]> userResults = usersRepository.getUserInfoStatusWithLocationBatch(userIds, currentUserLat, currentUserLong);
        Map<Long, InfoUserStatusWithLocation> userInfoMap = new HashMap<>();
        for (Object[] result : userResults) {
            Long userId = ((Number) result[0]).longValue();
            String phoneNo = (String) result[1];
            String uniqueName = (String) result[2];
            String coverImgUrl = (String) result[3];
            String createdAt = (String) result[4];
            String fullName = (String) result[5];
            String proPicsImgUrl = (String) result[6];
            Double latitude = result[7] != null ? ((Number) result[7]).doubleValue() : null;
            Double longitude = result[8] != null ? ((Number) result[8]).doubleValue() : null;
            Double distanceInMiles = result[9] != null ? ((Number) result[9]).doubleValue() : null;

            InfoUserStatusWithLocation userInfo = new InfoUserStatusWithLocation(
                    userId, phoneNo, uniqueName, coverImgUrl, createdAt, fullName, proPicsImgUrl,
                    latitude, longitude, distanceInMiles
            );
            userInfoMap.put(userId, userInfo);
        }

        // Group statuses by userId
        Map<Long, List<Status>> statusesByUser = statuses.stream()
                .collect(Collectors.groupingBy(Status::getUserId));

        // Create a list of UserWithStatusesResponse
        List<UserWithStatusesResponse> response = new ArrayList<>();
        for (Long userId : statusesByUser.keySet()) {
            InfoUserStatusWithLocation userInfo = userInfoMap.get(userId);
            if (userInfo == null) {
                // If we don't have user info, skip or create a default? Maybe skip.
                continue;
            }
            List<StatusResponse> statusResponses = statusesByUser.get(userId).stream()
                    .sorted(Comparator.comparing(Status::getStatusDateTime).reversed()
                            .thenComparing(Status::getStatusId).reversed())
                    .map(status -> new StatusResponse(
                            status.getStatusId(),
                            status.getContent(),
                            status.getStatusDateTime(),
                            status.getHashKeywords(),
                            status.getImages()
                    ))
                    .collect(Collectors.toList());
            response.add(new UserWithStatusesResponse(userInfo, statusResponses));
        }

        return response;
    }

}

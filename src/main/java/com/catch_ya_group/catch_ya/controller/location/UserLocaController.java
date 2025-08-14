package com.catch_ya_group.catch_ya.controller.location;

import com.catch_ya_group.catch_ya.modal.dto.LocationRequestDTO;
import com.catch_ya_group.catch_ya.modal.dto.UserLocaDTO;
import com.catch_ya_group.catch_ya.modal.dto.UserLocaResponseDTO;
import com.catch_ya_group.catch_ya.modal.entity.UserLoca;
import com.catch_ya_group.catch_ya.modal.projection.UserLocaResponseProjection;
import com.catch_ya_group.catch_ya.service.location.UserLocaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/private/v1/location")
@RequiredArgsConstructor
@Tag(
        name = "Location",
        description = "Endpoints for accessing and updating user location data, including geospatial queries and tracking."
)
public class UserLocaController {

    private final UserLocaService userLocaService;

    @PostMapping("/save")
    public ResponseEntity<UserLocaDTO> saveLocation(@RequestBody LocationRequestDTO request) {
        UserLoca saved = userLocaService.saveUserLocation(request.userId(), request.longitude(), request.latitude());
        return ResponseEntity.ok(UserLocaDTO.fromEntity(saved));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<UserLocaResponseDTO>> findNearbyUsers(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam double distanceMeters,
            @RequestParam Long userId) {
        List<UserLocaResponseProjection> nearby = userLocaService.findNearbyUsers(longitude, latitude, distanceMeters, userId);
        List<UserLocaResponseDTO> dtoList = nearby.stream().map(p -> UserLocaResponseDTO.builder()
                        .userId(p.getUserId())
                        .longitude(p.getLongitude())
                        .latitude(p.getLatitude())
                        .phoneNo(p.getPhoneNo())
                        .uniqueName(p.getUniqueName())
                        .fullName(p.getFullName())
                        .proPicsImgUrl(p.getProPicsImgUrl())
                        .createdAt(p.getCreatedAt())
                        .viewedCnt(p.getViewedCnt())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }


    @GetMapping("/{currentUserId}")
    public ResponseEntity<?> getCurrentUserLoca(@PathVariable Long currentUserId){
        System.out.println("Searching for userID: " + currentUserId);
        return ResponseEntity.ok(userLocaService.getCurrentUserLoca(currentUserId));
    }
}

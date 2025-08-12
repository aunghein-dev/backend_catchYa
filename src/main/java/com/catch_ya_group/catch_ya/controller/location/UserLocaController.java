package com.catch_ya_group.catch_ya.controller.location;

import com.catch_ya_group.catch_ya.modal.dto.LocationRequestDTO;
import com.catch_ya_group.catch_ya.modal.entity.UserLoca;
import com.catch_ya_group.catch_ya.service.location.UserLocaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/private/v1/location/")
@RequiredArgsConstructor
public class UserLocaController {

    private final UserLocaService userLocaService;

    @PostMapping("/save")
    public ResponseEntity<UserLoca> saveLocation(@RequestBody LocationRequestDTO request) {
        UserLoca saved = userLocaService.saveUserLocation(request.getUserId(), request.getLongitude(), request.getLatitude());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<UserLoca>> getNearbyUsers(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam(defaultValue = "1000") double distanceInMeters
    ) {
        List<UserLoca> nearbyUsers = userLocaService.findNearbyUsers(longitude, latitude, distanceInMeters);
        return ResponseEntity.ok(nearbyUsers);
    }

}
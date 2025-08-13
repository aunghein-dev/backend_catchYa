package com.catch_ya_group.catch_ya.service.location;

import com.catch_ya_group.catch_ya.modal.entity.UserLoca;
import com.catch_ya_group.catch_ya.modal.projection.UserLocaResponseProjection;
import com.catch_ya_group.catch_ya.repository.UserLocaRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLocaService {

    private final UserLocaRepository userLocaRepository;

    // Initialize GeometryFactory with SRID 4326
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public UserLoca saveUserLocation(Long userId, double longitude, double latitude) {
        validateCoordinates(longitude, latitude);

        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        UserLoca oldLoca = userLocaRepository.findByUserId(userId);

        if (oldLoca != null) {
            oldLoca.setLocation(point);
            return userLocaRepository.save(oldLoca);
        } else {
            UserLoca newLoca = UserLoca.builder()
                    .userId(userId)
                    .location(point)
                    .build();
            return userLocaRepository.save(newLoca);
        }
    }

    public List<UserLocaResponseProjection> findNearbyUsers(double longitude, double latitude, double distanceMeters, Long currentUserId) {
        validateCoordinates(longitude, latitude);
        return userLocaRepository.findNearbyUsers(longitude, latitude, distanceMeters, currentUserId);
    }


    private void validateCoordinates(double longitude, double latitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Invalid latitude: must be between -90 and 90");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Invalid longitude: must be between -180 and 180");
        }
    }

    public UserLocaResponseProjection getCurrentUserLoca(Long currentUserId) {
        return userLocaRepository.getCurrentUserLoca(currentUserId);
    }
}

package com.catch_ya_group.catch_ya.service.location;

import com.catch_ya_group.catch_ya.modal.entity.UserLoca;
import com.catch_ya_group.catch_ya.repository.UserLocaRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLocaService {

    private final UserLocaRepository userLocaRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    public UserLoca saveUserLocation(Long userId, double longitude, double latitude) {
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

    public List<UserLoca> findNearbyUsers(double longitude, double latitude, double distanceMeters) {
        return userLocaRepository.findNearbyUsers(longitude, latitude, distanceMeters);
    }
}

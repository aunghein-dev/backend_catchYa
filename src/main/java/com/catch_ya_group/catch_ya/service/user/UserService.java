package com.catch_ya_group.catch_ya.service.user;


import com.catch_ya_group.catch_ya.modal.dto.ProfileUpdaterRequest;
import com.catch_ya_group.catch_ya.modal.dto.UserLoginDTO;
import com.catch_ya_group.catch_ya.modal.entity.Leaderboard;
import com.catch_ya_group.catch_ya.modal.entity.UserInfos;
import com.catch_ya_group.catch_ya.modal.entity.Users;
import com.catch_ya_group.catch_ya.repository.LeaderboardRepository;
import com.catch_ya_group.catch_ya.repository.UserInfosRepository;
import com.catch_ya_group.catch_ya.repository.UsersRepository;
import com.catch_ya_group.catch_ya.service.auth.JWTService;
import com.catch_ya_group.catch_ya.service.file.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JWTService jwtService;
    private final UserInfosRepository userInfosRepo;
    private final LeaderboardRepository leaderboardRepo;
    private final UsersRepository usersRepository;
    private final AuthenticationManager authManager;
    private final BCryptPasswordEncoder encoder;
    private final MinioService minioService;

    public Users register(Users user, UserInfos userInfos, Leaderboard leaderboard){
        // Save dependent entities first
        userInfos.setUserInfoId(null);
        user.setUserId(null);
        leaderboard.setLeaderboardId(null);
        UserInfos savedUserInfos = userInfosRepo.save(userInfos);
        Leaderboard savedLeaderboard = leaderboardRepo.save(leaderboard);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setUserInfos(savedUserInfos);
        user.setLeaderboard(savedLeaderboard);
        return usersRepository.save(user);
    }

    public List<Users> getUsers() {
        return usersRepository.findAll();
    }

    public String verify(UserLoginDTO user) {
        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(
                        user.getPhoneNo(), user.getPassword()
                ));
        return authentication.isAuthenticated()? jwtService.generateToken(user.getPhoneNo()) : "fail";
    }

    public boolean checkUserAlreadyExits(String phoneNo) {
        return usersRepository.existsByPhoneNo(phoneNo);
    }

    public Users resetPassword(Long id, String newPassword) {
        Users user = usersRepository.findById(id).orElseThrow();
        user.setPassword(encoder.encode(newPassword));
        return usersRepository.save(user);
    }

    public boolean existsByUniqueName(String uniqueName) {
        return usersRepository.existsByUniqueName(uniqueName);
    }

    public Long findUserIdByPhoneNo(String phoneNo) {
        return usersRepository.findUserIdByPhoneNo(phoneNo);
    }

    public boolean checkPasswordCorrect(Long userId, String oldPassword) {
        Users user = usersRepository.findById(userId).orElseThrow();
        return encoder.matches(oldPassword, user.getPassword());
    }

    public Users changePassword(Long userId, String newPassword) {
        Users user = usersRepository.findById(userId).orElseThrow();
        user.setPassword(encoder.encode(newPassword));
        usersRepository.save(user);
        return user;
    }

    @Transactional
    public String updateUserImage(Long userId, MultipartFile file, ImageType type) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id " + userId));

        UserInfos info = userInfosRepo.findById(user.getUserInfos().getUserInfoId())
                .orElseThrow(() -> new IllegalArgumentException("User info not found for userId " + userId));

        // Get old image URL based on type
        String oldUrl = (type == ImageType.COVER) ? info.getCoverImgUrl() : info.getProPicsImgUrl();

        // Delete old image if exists
        if (oldUrl != null && !oldUrl.isBlank()) {
            try {
                minioService.deleteFile(oldUrl);
            } catch (IllegalArgumentException ignore) {
                // old file not found → safe to ignore
            } catch (Exception e) {
                throw new RuntimeException("Failed to delete old " + type.name().toLowerCase() + " image", e);
            }
        }

        // Upload new image
        String newUrl;
        try {
            newUrl = minioService.uploadFile(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload new " + type.name().toLowerCase() + " image", e);
        }

        // Update field dynamically
        if (type == ImageType.COVER) {
            info.setCoverImgUrl(newUrl);
        } else {
            info.setProPicsImgUrl(newUrl);
        }

        userInfosRepo.save(info);

        return newUrl;
    }

    @Transactional
    public Users updateProfileData(ProfileUpdaterRequest request) {
        Users user = usersRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id " + request.userId()));

        // Update user data
        user.setUniqueName(request.uniqueName());

        // Update user info directly (no need to re-fetch)
        UserInfos info = user.getUserInfos();
        if (info == null) {
            throw new IllegalStateException("User info not found for userId " + request.userId());
        }
        info.setFullName(request.fullName());

        // Because of @Transactional, changes are flushed automatically
        return usersRepository.save(user);
    }

    public enum ImageType {
        COVER, PROFILE
    }
}


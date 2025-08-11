package com.catch_ya_group.catch_ya.service.user;


import com.catch_ya_group.catch_ya.modal.dto.UserLoginDTO;
import com.catch_ya_group.catch_ya.modal.entity.Leaderboard;
import com.catch_ya_group.catch_ya.modal.entity.UserInfos;
import com.catch_ya_group.catch_ya.modal.entity.Users;
import com.catch_ya_group.catch_ya.repository.LeaderboardRepository;
import com.catch_ya_group.catch_ya.repository.UserInfosRepository;
import com.catch_ya_group.catch_ya.repository.UsersRepository;
import com.catch_ya_group.catch_ya.service.auth.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JWTService jwtService;
    private final UserInfosRepository userInfosRepo;
    private final LeaderboardRepository leaderboardRepo;
    private final UsersRepository usersRepository;
    AuthenticationManager authManager;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

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
}


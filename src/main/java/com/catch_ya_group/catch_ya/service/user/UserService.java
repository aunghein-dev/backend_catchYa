package com.catch_ya_group.catch_ya.service.user;


import com.catch_ya_group.catch_ya.modal.entity.Users;
import com.catch_ya_group.catch_ya.repository.UserRepo;
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

    private final UserRepo userRepo;
    private final JWTService jwtService;
    AuthenticationManager authManager;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public Users register(Users user){
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    public List<Users> getUsers() {
        return userRepo.findAll();
    }

    public String verify(Users user) {
        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(
                        user.getPhoneNo(), user.getPassword()
                ));
        return authentication.isAuthenticated()? jwtService.generateToken(user.getPhoneNo()) : "fail";
    }

    public boolean checkUserAlreadyExits(String phoneNo) {
        return userRepo.existsByPhoneNo(phoneNo);
    }

    public Users resetPassword(Long id, String newPassword) {
        Users user = userRepo.findById(id).orElseThrow();
        user.setPassword(encoder.encode(newPassword));
        return userRepo.save(user);
    }
}
package com.catch_ya_group.catch_ya.service.auth;

import com.catch_ya_group.catch_ya.modal.entity.Users;
import com.catch_ya_group.catch_ya.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Users user = repo.findByPhoneNo(username);


        return new org.springframework.security.core.userdetails.User(
                user.getPhoneNo(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("USER"))
        );
    }

}
package com.fzemi.workshopmanager.user.service.impl;

import com.fzemi.workshopmanager.user.entity.User;
import com.fzemi.workshopmanager.user.repository.UserRepository;
import com.fzemi.workshopmanager.user.roles.UserRoles;
import com.fzemi.workshopmanager.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createUserDev(UserRoles role) {
        User user = User.builder()
                .firstname("John")
                .surname("Doe")
                .email("john.doe@email.com")
                .username("admin")
                .password("admin")
                .isAccountLocked(false)
                .roles(Set.of(role))
                .build();

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}

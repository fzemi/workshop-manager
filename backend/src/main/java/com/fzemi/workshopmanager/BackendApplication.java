package com.fzemi.workshopmanager;

import com.fzemi.workshopmanager.file.service.FileStorageService;
import com.fzemi.workshopmanager.user.roles.UserRoles;
import com.fzemi.workshopmanager.user.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    @Profile("dev")
    CommandLineRunner init(
            FileStorageService fileStorageService,
            UserService userService
    ) {
        return args -> {
            fileStorageService.deleteAll();
            fileStorageService.init();

            userService.createUserDev(UserRoles.ADMIN);
        };
    }

}

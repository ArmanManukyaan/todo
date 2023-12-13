package com.example.todorest.config;

import com.example.todorest.entity.User;
import com.example.todorest.entity.UserType;
import com.example.todorest.security.CurrentUser;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.List;

@TestConfiguration
public class SpringSecurityWebAuxTestConfig {

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        User user = User.builder()
                .id(1)
                .email("armanmanukyan@mail.com")
                .name("poxos")
                .surname("poxosyan")
                .password("poxosi")
                .token(null)
                .enabled(true)
                .userType(UserType.ADMIN).build();
        CurrentUser currentUser = new CurrentUser(user);


        return new InMemoryUserDetailsManager(List.of(currentUser));
    }

}

package com.example.todorest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.security.test.context.support.WithSecurityContext;

@SpringBootTest
@Profile("application-test.properties")
class TodoRestApplicationTests {

    @Test
    void contextLoads() {
    }

}

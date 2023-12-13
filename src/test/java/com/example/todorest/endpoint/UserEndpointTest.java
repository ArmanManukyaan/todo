package com.example.todorest.endpoint;

import com.example.todorest.config.SpringSecurityWebAuxTestConfig;
import com.example.todorest.dto.CreateUserDto;
import com.example.todorest.dto.UpdateUserDto;
import com.example.todorest.dto.UserAuthReqDto;
import com.example.todorest.entity.User;
import com.example.todorest.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.example.todorest.entity.UserType.USER;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SpringSecurityWebAuxTestConfig.class)
class UserEndpointTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    public void after() {
        userRepository.deleteAll();
    }


    @Test
    void auth() throws Exception {
        String password = "123456";
        String email = "poxos@mail.com";
        String encodePassword = passwordEncoder.encode(password);
        User user = User.builder()
                .id(1)
                .name("poxos")
                .surname("poxosyan")
                .email(email)
                .password(encodePassword)
                .token(null)
                .enabled(true)
                .userType(USER)
                .build();
        userRepository.save(user);
        UserAuthReqDto authReqDto = new UserAuthReqDto(email, password);
        mvc.perform(MockMvcRequestBuilders.post("/user/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authReqDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").isNotEmpty());
    }


    @Test
    void testAuthenticationFailure() throws Exception {
        String email = "armanmanukyan@mail.com";
        String password = "wrongPassword";
        UserAuthReqDto authReqDto = new UserAuthReqDto(email, password);
        mvc.perform(MockMvcRequestBuilders.post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authReqDto)))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void addUser() throws Exception {
        CreateUserDto validUserRegisterDto = createValidUserRegisterDto();
        mvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserRegisterDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("poxos@mail.com"));
    }


    @Test
    void addUserWithUnValid() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto();
        mvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isBadRequest());
    }

    private CreateUserDto createValidUserRegisterDto() {
        return CreateUserDto.builder()
                .id(1)
                .name("Poxos")
                .surname("Poxosyan")
                .email("poxos@mail.com")
                .enabled(true)
                .userType(USER)
                .password(passwordEncoder.encode("123456"))
                .build();
    }


    private UpdateUserDto updateUserDtoValid() {
        return UpdateUserDto.builder()
                .name("Petros")
                .surname("Petrosyan")
                .email("petros@mai.com")
                .password(passwordEncoder.encode("12345"))
                .build();
    }


    private User getUser() {
        return User.builder()
                .id(1)
                .name("Poxos")
                .surname("Poxosyan")
                .email("petros@mail.com")
                .enabled(true)
                .userType(USER)
                .password(passwordEncoder.encode("123456"))
                .build();
    }
}
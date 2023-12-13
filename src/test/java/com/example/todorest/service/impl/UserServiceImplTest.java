package com.example.todorest.service.impl;

import com.example.todorest.component.EmailSenderService;
import com.example.todorest.component.UserFilterManager;
import com.example.todorest.dto.CreateUserDto;
import com.example.todorest.dto.UpdateUserDto;
import com.example.todorest.dto.UserDto;
import com.example.todorest.dto.UserSearchDto;
import com.example.todorest.entity.User;
import com.example.todorest.entity.UserType;
import com.example.todorest.mapper.UserMapper;
import com.example.todorest.repository.UserRepository;
import com.example.todorest.security.CurrentUser;
import com.example.todorest.service.UserService;
import com.example.todorest.util.ImageDownloader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    private final UserMapper userMapper = Mockito.mock(UserMapper.class);
    private final EmailSenderService emailSenderService = Mockito.mock(EmailSenderService.class);
    private final ImageDownloader imageDownloader = Mockito.mock(ImageDownloader.class);
    private final UserFilterManager userFilterManager = Mockito.mock(UserFilterManager.class);
    private final MultipartFile multipartFile = mock(MultipartFile.class);
    private final CurrentUser currentUser = mock(CurrentUser.class);
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, userMapper,
                emailSenderService, imageDownloader, userFilterManager);
    }


    @Test
    void save() {
        User user = getUser();
        UserDto userDto = getUserDto();
        CreateUserDto createUserDto = getCreateUserDto();
        when(userMapper.map(createUserDto)).thenReturn(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.mapDto(user)).thenReturn(userDto);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodePassword");
        UserDto result = userService.save(createUserDto);

        Assertions.assertEquals(result.getId(), userDto.getId());
        Assertions.assertNotNull(result);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).mapDto(user);
        verify(passwordEncoder, times(1)).encode(createUserDto.getPassword());
    }


    @Test
    void update() {
        User user = getUser();
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .email("petros@mail.com")
                .name("petros")
                .surname("petrosyan")
                .password("121343435")
                .build();
        CurrentUser currentUser = new CurrentUser(getUser());

        UserDto userDto = UserDto.builder()
                .email(updateUserDto.getEmail())
                .name(updateUserDto.getName())
                .surname(updateUserDto.getSurname())
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(updateUserDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(updateUserDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.mapDto(user)).thenReturn(userDto);

        Optional<UserDto> result = userService.update(updateUserDto, user.getId(), currentUser);

        Assertions.assertTrue(result.isPresent());
        UserDto resultUserDto = result.get();
        Assertions.assertEquals(userDto.getId(), resultUserDto.getId());
        Assertions.assertEquals(userDto.getEmail(), resultUserDto.getEmail());
        Assertions.assertEquals(userDto.getName(), resultUserDto.getName());
        Assertions.assertEquals(userDto.getSurname(), resultUserDto.getSurname());

        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).findByEmail(updateUserDto.getEmail());
        verify(passwordEncoder, times(1)).encode(updateUserDto.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void verifyAccount() {
        String email = "poxos@mail.com";
        String token = "ss@dcdd0!@ded";

        User userFromDb = User.builder()
                .id(1)
                .email(email)
                .token(token)
                .enabled(false)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userFromDb));
        when(userRepository.save(userFromDb)).thenReturn(userFromDb);
        User result = userService.verifyAccount(email, token);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEnabled());
        Assertions.assertNull(result.getToken());

        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).save(userFromDb);

    }


    @Test
    void search() {
        int size = 5;
        int page = 0;
        UserSearchDto searchDto = new UserSearchDto();

        List<UserDto> getListUserDto = List.of(getUserDto());
        List<User> getListUser = List.of(getUser());

        when(userFilterManager.searchUserByFilter(size, page, searchDto)).thenReturn(getListUser);
        when(userMapper.userDtoList(getListUser)).thenReturn(getListUserDto);
        List<UserDto> result = userService.search(size, page, searchDto);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(getListUserDto.size(), result.size());

        verify(userFilterManager, times(1)).searchUserByFilter(size, page, searchDto);
        verify(userMapper, times(1)).userDtoList(getListUser);
    }


    @Test
    void passwordChange() {
        String email = "poxos@mail.com";
        String token = "ss@dcdd0!@ded";

        User userFromDb = new User();
        userFromDb.setToken(null);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userFromDb));
        when(userRepository.save(userFromDb)).thenReturn(userFromDb);
        boolean result = userService.passwordChange(email, token);

        Assertions.assertTrue(result);
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).save(userFromDb);
    }

    @Test
    void confirmationMessage() {
        String email = "poxos@mail.com";

        User userFromDb = new User();
        UUID token = UUID.randomUUID();
        userFromDb.setToken(token.toString());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userFromDb));
        when(userRepository.save(userFromDb)).thenReturn(userFromDb);
        boolean result = userService.confirmationMessage(email);

        Assertions.assertTrue(result);
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).save(userFromDb);
    }

    @Test
    void resetPassword() {
        String email = "poxos@mail.com";
        String password = "1234567";
        String passwordRepeat = "1234567";

        User userFromDb = new User();
        userFromDb.setPassword(passwordEncoder.encode(password));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userFromDb));
        when(passwordEncoder.encode(password)).thenReturn("encodePassword");
        when(userRepository.save(userFromDb)).thenReturn(userFromDb);
        boolean result = userService.resetPassword(email, password, passwordRepeat);

        Assertions.assertTrue(result);
        Assertions.assertNotNull(email);
        Assertions.assertEquals(password, passwordRepeat);

        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).save(userFromDb);
    }

    @Test
    void uploadImageForUser() throws IOException {
        int userId = 1;
        User userFromDb = new User();
        userFromDb.setId(userId);

        when(currentUser.getUser()).thenReturn(userFromDb);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userFromDb));
        when(userMapper.mapDto(userFromDb)).thenReturn(getUserDto());

        Optional<UserDto> result = userService.uploadImageForUser(userId, multipartFile, currentUser);

        Assertions.assertTrue(result.isPresent());
        verify(imageDownloader).saveProfilePicture(multipartFile, userFromDb);
        verify(userRepository).save(userFromDb);
        verify(userMapper).mapDto(userFromDb);
    }

    private User getUser() {
        return User.builder()
                .id(1)
                .name("Poxos")
                .surname("Poxosyan")
                .email("poxos@mail.com")
                .password("1234567")
                .userType(UserType.USER)
                .enabled(true)
                .build();

    }

    private UserDto getUserDto() {
        return UserDto.builder()
                .id(1)
                .name("Poxos")
                .surname("Poxosyan")
                .email("poxos@mail.com")
                .userType(UserType.USER)
                .enabled(true)
                .build();
    }

    private CreateUserDto getCreateUserDto() {
        return CreateUserDto.builder()
                .id(1)
                .name("Poxos")
                .surname("Poxosyan")
                .email("poxos@mail.com")
                .password("1234567")
                .userType(UserType.USER)
                .enabled(true)
                .build();
    }


}
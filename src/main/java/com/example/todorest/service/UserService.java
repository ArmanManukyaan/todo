package com.example.todorest.service;

import com.example.todorest.dto.CreateUserDto;
import com.example.todorest.dto.UpdateUserDto;
import com.example.todorest.dto.UserDto;
import com.example.todorest.dto.UserSearchDto;
import com.example.todorest.entity.User;
import com.example.todorest.security.CurrentUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findByEmail(String email);

    UserDto save(CreateUserDto userDto);

    UserDto findById(int id);

    Optional<UserDto> update(UpdateUserDto updateUserDto, int id, CurrentUser currentUser);

    void deleteById(int id) throws IOException;

    void sendEmailVerificationMessage(int id);

    User verifyAccount(String email, String token);

    Optional<UserDto> uploadImageForUser(int id, MultipartFile multipartFile, CurrentUser currentUser);

    boolean activateDeactivate(int id);

    boolean resetPassword(String email, String password, String passwordRepeat);

    void changUserStatus(User user);

    boolean confirmationMessage(String email);

    boolean passwordChange(String email, String token);

    List<UserDto> search(int size, int page, UserSearchDto searchDto);
}

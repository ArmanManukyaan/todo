package com.example.todorest.endpoint;

import com.example.todorest.dto.*;
import com.example.todorest.entity.User;
import com.example.todorest.jwtUtil.JwtTokenUtil;
import com.example.todorest.mapper.UserMapper;
import com.example.todorest.security.CurrentUser;
import com.example.todorest.service.UserService;
import com.example.todorest.validation.ValidationCheck;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserEndpoint {
    private final UserMapper userMapper;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    @Value("${todo.upload.image.pat}")
    private String imageUploadPath;


    @PostMapping("/auth")
    public ResponseEntity<UserAuthRestDto> auth(@RequestBody UserAuthReqDto authReqDto) {
        Optional<User> byEmail = userService.findByEmail(authReqDto.getEmail());
        if (byEmail.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = byEmail.get();
        if (!passwordEncoder.matches(authReqDto.getPassword(), user.getPassword()) || !user.isEnabled()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = jwtTokenUtil.generateToken(authReqDto.getEmail());
        return ResponseEntity.ok(new UserAuthRestDto(token));
    }

    @PostMapping("/search")
    public ResponseEntity<List<UserDto>> search(@RequestParam(name = "size", defaultValue = "5") int size,
                                                @RequestParam(value = "page", defaultValue = "0") int page,
                                                @RequestBody UserSearchDto searchDto) {
        return ResponseEntity.ok(userService.search(size, page, searchDto));
    }


    @PostMapping("/register")
    public ResponseEntity<?> addUser(@RequestBody @Valid CreateUserDto userDto,
                                     BindingResult bindingResult) {
        StringBuilder validationResult = ValidationCheck.checkValidation(bindingResult);
        return !validationResult.isEmpty() ? ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(validationResult.toString()) :
                ResponseEntity.ok(userService.save(userDto));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestBody @Valid UpdateUserDto updateUserDto,
                                    @PathVariable("id") int id,
                                    @AuthenticationPrincipal CurrentUser currentUser, BindingResult bindingResult) {
        StringBuilder validationResult = ValidationCheck.checkValidation(bindingResult);
        return !validationResult.isEmpty() ? ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(validationResult.toString()) :
                ResponseEntity.ok(userService.update(updateUserDto, id, currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") int id) {
        return ResponseEntity.ok(userService.findById(id));
    }


    @PostMapping("/activate-deactivate/{id}")
    public ResponseEntity<?> activateDeactivateUser(@PathVariable("id") int id) {
        return userService.activateDeactivate(id) ?
                ResponseEntity.ok(HttpStatus.ACCEPTED) :
                ResponseEntity.status(HttpStatus.CONFLICT).build();
    }


    @GetMapping("/verify-account")
    public ResponseEntity<UserVerifyDto> verifyUser(@RequestParam("email") String email,
                                                    @RequestParam("token") String token) {
        User user = userService.verifyAccount(email, token);
        return user != null ? ResponseEntity.ok(userMapper.userMap(user))
                : ResponseEntity.status(HttpStatus.CONFLICT).build();
    }


    @GetMapping("/confirm-email")
    public ResponseEntity<?> confirmEmailPasswordChang(@RequestParam("email") String email) {
        return userService.confirmationMessage(email)
                ? ResponseEntity.ok(HttpStatus.OK) :
                ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @GetMapping("/password-chang-page")
    public ResponseEntity<?> changPasswordPage(@RequestParam("email") String email,
                                               @RequestParam("token") String token) {
        return userService.passwordChange(email, token)
                ? ResponseEntity.ok(HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.CONFLICT).build();
    }


    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("email") String email,
                                           @RequestParam("password") String password,
                                           @RequestParam("passwordRepeat") String passwordRepeat
    ) {
        return userService.resetPassword(email, password, passwordRepeat)
                ? ResponseEntity.ok(HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.CONFLICT).build();
    }


    @PostMapping("/{id}/image")
    public ResponseEntity<UserDto> addImageUser(@RequestParam("image") MultipartFile multipartFile,
                                                @PathVariable("id") int id,
                                                @AuthenticationPrincipal CurrentUser currentUser) {
        Optional<UserDto> userDtoOptional = userService.uploadImageForUser(id, multipartFile, currentUser);
        return userDtoOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }


    @GetMapping(value = "/getImage",
            produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage(@RequestParam("picName") String picName) throws IOException {
        File file = new File(imageUploadPath + picName);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                return IOUtils.toByteArray(fis);
            }
        }
        return null;
    }


    @DeleteMapping("/delete/{id}")
    public void deleteById(@PathVariable("id") int id) throws IOException {
        userService.deleteById(id);
    }

}
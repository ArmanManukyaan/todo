package com.example.todorest.service.impl;

import com.example.todorest.component.EmailSenderService;
import com.example.todorest.component.UserFilterManager;
import com.example.todorest.dto.CreateUserDto;
import com.example.todorest.dto.UpdateUserDto;
import com.example.todorest.dto.UserDto;
import com.example.todorest.dto.UserSearchDto;
import com.example.todorest.entity.User;
import com.example.todorest.entity.UserType;
import com.example.todorest.exception.ImageProcessingException;
import com.example.todorest.exception.ServiceImplConflictException;
import com.example.todorest.exception.ServiceImplNotFundException;
import com.example.todorest.mapper.UserMapper;
import com.example.todorest.repository.UserRepository;
import com.example.todorest.security.CurrentUser;
import com.example.todorest.service.UserService;
import com.example.todorest.util.ImageDownloader;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final EmailSenderService emailSenderService;
    private final ImageDownloader imageDownloader;
    private final UserFilterManager userFilterManager;

    @Value("${site.url}")
    private String siteUrl;

    /**
     * This method finds a user by email using the userRepository.
     * It returns an Optional<User> representing the found user.
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Saves a new user based on the provided CreateUserDto.
     *
     * @param userDto The CreateUserDto containing user information.
     * @return UserDto representing the saved user.
     * @throws ServiceImplConflictException If a user with the same email already exists.
     *                                      Log the successful user registration
     */
    @Override
    public UserDto save(CreateUserDto userDto) {
        User user = userMapper.map(userDto);
        Optional<User> byEmail = userRepository.findByEmail(user.getEmail());
        if (byEmail.isPresent()) {
            throw new ServiceImplConflictException("The user is registered with this email");
        } else {
            String password = user.getPassword();
            String encodePassword = passwordEncoder.encode(password);
            user.setPassword(encodePassword);
            user.setUserType(UserType.USER);
            user.setEnabled(false);
            UUID token = UUID.randomUUID();
            user.setToken(token.toString());
            UserDto userDto1 = userMapper.mapDto(userRepository.save(user));
            log.info("User has successfully registered");
            sendEmailVerificationMessage(user.getId());
            return userDto1;
        }
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id The unique identifier of the user.
     * @return UserDto representing the found user.
     * @throws ServiceImplNotFundException no user is found with the given id.
     */

    @Override
    public UserDto findById(int id) {
        Optional<User> byId = userRepository.findById(id);
        if (byId.isEmpty()) {
            throw new ServiceImplNotFundException("User by id is empty");
        }
        return userMapper.mapDto(byId.get());
    }

    /**
     * Updates user information based on the provided UpdateUserDto and user ID.
     *
     * @param updateUserDto The UpdateUserDto containing updated user information.
     * @param id            The unique identifier of the user to be updated.
     * @param currentUser   The currently logged-in user.
     * @return Optional<UserDto> representing the updated user if successful, otherwise empty.
     * @throws ServiceImplConflictException If the specified condition for updating is not satisfied.
     * @throws ServiceImplNotFundException  If no user is found with the given id.
     */
    @Override
    public Optional<UserDto> update(UpdateUserDto updateUserDto, int id, CurrentUser currentUser) {
        if (currentUser.getUser().getId() != id) {
            throw new ServiceImplConflictException("The specified condition is not satisfied");
        } else {
            Optional<User> byId = userRepository.findById(id);
            if (byId.isEmpty()) {
                throw new ServiceImplNotFundException("User by id is empty");
            } else {
                User userFromDb = byId.get();
                if (userRepository.findByEmail(updateUserDto.getEmail()).isEmpty() ||
                        updateUserDto.getEmail().equals(userFromDb.getEmail())) {
                    userFromDb.setName(updateUserDto.getName());
                    userFromDb.setSurname(updateUserDto.getSurname());
                    userFromDb.setEmail(updateUserDto.getEmail());
                    userFromDb.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
                    UserDto userDto = userMapper.mapDto(userRepository.save(userFromDb));
                    log.info("User with the " + userFromDb.getId() + " id was updated");
                    return Optional.of(userDto);
                }
            }
            return Optional.empty();
        }
    }

    /**
     * Deletes a user by their unique identifier, including associated profile picture.
     *
     * @param id The unique identifier of the user to be deleted.
     * @throws ServiceImplNotFundException If no user is found with the given id.
     * @throws java.io.IOException         If an I/O error occurs during profile picture deletion.
     */
    @Override
    public void deleteById(int id) throws java.io.IOException {
        Optional<User> byId = userRepository.findById(id);
        if (byId.isEmpty()) {
            throw new ServiceImplNotFundException("User By id is empty");
        } else {
            imageDownloader.deleteProfilePicture(byId.get().getPicName());
            userRepository.deleteById(byId.get().getId());
            log.info("User with the " + id + " id was deleted");
        }
    }

    /**
     * Verifies a user's account based on the provided email and token.
     *
     * @param email The email associated with the user's account.
     * @param token The verification token.
     * @return The updated User object after account verification.
     * @throws ServiceImplConflictException If the specified conditions for account verification are not met.
     */
    public User verifyAccount(String email, String token) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent() &&
                (!byEmail.get().getToken().equals(token)) &&
                byEmail.get().isEnabled()) {
            throw new ServiceImplConflictException("The condition does not mee the specified conditions");
        } else {
            User user = byEmail.get();
            user.setEnabled(true);
            user.setToken(null);
            log.info("User  with the  " + user.getId() + " id has verified his account");
            return userRepository.save(user);

        }

    }

    /**
     * Uploads a profile image for a user based on the provided user ID, image file, and current user.
     *
     * @param id            The unique identifier of the user for whom the image is being uploaded.
     * @param multipartFile The MultipartFile representing the uploaded image file.
     * @param currentUser   The currently logged-in user.
     * @return Optional<UserDto> representing the user with the updated profile image if successful, otherwise empty.
     * @throws ServiceImplConflictException If the user ID from the current user and the provided ID are incompatible.
     * @throws ImageProcessingException     If an error occurs during image processing or uploading.
     */
    @Override
    public Optional<UserDto> uploadImageForUser(int id, MultipartFile multipartFile, CurrentUser currentUser) {
        try {
            if (currentUser.getUser().getId() != id) {
                throw new ServiceImplConflictException("The user Id and the Id taken by the parameter are incompatible");
            } else {
                Optional<User> userOptional = userRepository.findById(id);
                User user = userOptional.get();
                imageDownloader.saveProfilePicture(multipartFile, userOptional.get());
                userRepository.save(user);
                UserDto userDto = userMapper.mapDto(user);
                log.info("uploadImageForUser() in UserServiceImpl has successfully worked");
                return Optional.of(userDto);
            }
        } catch (IOException | java.io.IOException e) {
            throw new ImageProcessingException("Image uploading failed");
        }
    }

    /**
     * Activates or deactivates a user based on the provided user ID.
     *
     * @param id The unique identifier of the user to be activated or deactivated.
     * @return true if the activation or deactivation is successful, false otherwise.
     * @throws ServiceImplNotFundException If no user is found with the given id.
     */
    @Override
    public boolean activateDeactivate(int id) {
        boolean isActivateDeactivateUser;
        Optional<User> byId = userRepository.findById(id);
        if (byId.isEmpty()) {
            throw new ServiceImplNotFundException("User by id is empty");
        } else {
            User user = byId.get();
            changUserStatus(user);
            userRepository.save(user);
            isActivateDeactivateUser = true;
            log.info("activateDeactivate() in UserServiceImpl has successfully worked");
        }
        return isActivateDeactivateUser;
    }

    /**
     * Resets the password for a user based on the provided email, new password, and password repeat.
     *
     * @param email          The email associated with the user's account.
     * @param password       The new password.
     * @param passwordRepeat The repeated entry of the new password for verification.
     * @return true if the password reset is successful, false otherwise.
     * @throws ServiceImplConflictException If the specified conditions for password reset are not met.
     */
    @Override
    public boolean resetPassword(String email, String password, String passwordRepeat) {
        boolean isResetPassword;
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isEmpty() && !password.equals(passwordRepeat) &&
                byEmail.get().getToken() != null && !byEmail.get().isEnabled()) {
            throw new ServiceImplConflictException("The condition does not mee the specified conditions");
        } else {
            User user = byEmail.get();
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            isResetPassword = true;
            log.info("resetPassword() in UserServiceImpl successfully worked ");
        }
        return isResetPassword;
    }

    /**
     * Changes the user status, toggling between active and inactive, and updates the user's token accordingly.
     * Sends appropriate messages based on the status change.
     *
     * @param user The user whose status is to be changed.
     */
    @Override
    public void changUserStatus(User user) {
        if (user.isEnabled()) {
            user.setEnabled(false);
            UUID token = UUID.randomUUID();
            user.setToken(token.toString());
            sendBlockMessage(user);
        } else {
            user.setEnabled(true);
            user.setToken(null);
            sendActivationMessage(user);
        }
    }

    /**
     * Sends a confirmation message to the user based on the provided email.
     * Generates a new token, updates the user's token, and sends a message for a specific action (e.g., reset password).
     *
     * @param email The email associated with the user's account.
     * @return true if the confirmation message is successfully sent, false otherwise.
     * @throws ServiceImplNotFundException If no user is found with the given email.
     */
    @Override
    public boolean confirmationMessage(String email) {
        boolean isConfirmation;
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isEmpty()) {
            throw new ServiceImplNotFundException("User By email empty");
        } else {
            User user = byEmail.get();
            UUID token = UUID.randomUUID();
            user.setToken(token.toString());
            userRepository.save(user);
            sendEmailResetPassword(user.getId());
            isConfirmation = true;
            log.info("confirmationMessage() in UserServiceImpl has successfully worked");

        }
        return isConfirmation;
    }

    /**
     * Changes the user's password based on the provided email and token.
     *
     * @param email The email associated with the user's account.
     * @param token The verification token.
     * @return true if the password change is successful, false otherwise.
     * @throws ServiceImplConflictException If the specified conditions for password change are not met.
     */
    @Override
    public boolean passwordChange(String email, String token) {
        boolean isPasswordChange;
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isEmpty() &&
                !byEmail.get().getToken().equals(token)) {
            throw new ServiceImplConflictException("The condition is not satisfied");
        } else {
            User user = byEmail.get();
            user.setToken(null);
            userRepository.save(user);
            isPasswordChange = true;
            log.info("passwordChange() in UserServiceImpl has successfully worked");
        }
        return isPasswordChange;
    }

    /**
     * Searches for users based on the provided search criteria.
     *
     * @param size      The number of users to retrieve in each page.
     * @param page      The page number of the results.
     * @param searchDto The UserSearchDto containing search criteria.
     * @return List of UserDto representing the users that match the search criteria.
     * @throws ServiceImplNotFundException If the search results are empty.
     */
    @Override
    public List<UserDto> search(int size, int page, UserSearchDto searchDto) {
        List<User> usersList = userFilterManager.searchUserByFilter(size, page, searchDto);
        if (usersList.isEmpty()) {
            throw new ServiceImplNotFundException("UserList is empty");
        } else {
            return userMapper.userDtoList(usersList);
        }
    }


    public void sendEmailResetPassword(int id) {
        Optional<User> byId = userRepository.findById(id);
        if (byId.isPresent()) {
            User user = byId.get();
            emailSenderService.setJavaMailSender(user.getEmail(),
                    "Welcome", "Hi" + user.getName() +
                            "\n" + "Confirm to rest password " +
                            siteUrl + "/user/password-chang-page?email=" + user.getEmail() + "&token=" + user.getToken());
            log.info("A message wil be sent to change the password");
        }
    }

    private void sendActivationMessage(User user) {
        emailSenderService.setJavaMailSender(user.getEmail(),
                "You are unblocked", "Hi" + user.getName() +
                        "\n" + "You are active again");
        log.info("Activation message  was send to the user with " + user.getId() + " id");
    }

    private void sendBlockMessage(User user) {
        emailSenderService.setJavaMailSender(user.getEmail(),
                "You are blocked ", "Hi" + user.getName() +
                        "\n" + "You are deactivated by Admin");
        log.info("Deactivation message  was send to the user with " + user.getId() + " id");
    }

    @Async
    public void sendEmailVerificationMessage(int id) {
        Optional<User> byId = userRepository.findById(id);
        if (byId.isPresent()) {
            User user = byId.get();
            emailSenderService.setJavaMailSender(user.getEmail(),
                    "Welcome ", "Hi" + user.getName() +
                            "\n" + "Please verify your email by clicking on this url :" +
                            siteUrl + "/user/verify-account?email=" + user.getEmail() + "&token=" + user.getToken());
            log.info("User's verification message has been send to the user with the  " + id + " id");
        }

    }
}





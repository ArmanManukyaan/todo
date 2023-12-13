package com.example.todorest.mapper;

import com.example.todorest.dto.CreateUserDto;
import com.example.todorest.dto.UserDto;
import com.example.todorest.dto.UserStatusDto;
import com.example.todorest.dto.UserVerifyDto;
import com.example.todorest.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Optional;
/**
 * Mapper class for mapping between CreateUserDto, User, UserStatusDto, UserDto, and UserVerifyDto entities.
 */
@Mapper(componentModel = "spring")
public abstract class UserMapper {
    @Value("${site.url}")
    public String siteUrl;

    /**
     * Maps a CreateUserDto to a User.
     *
     * @param dto The CreateUserDto to be mapped.
     * @return The mapped User.
     */
    public abstract User map(CreateUserDto dto);
    /**
     * Maps an Optional<User> to a UserStatusDto.
     *
     * @param user The Optional<User> to be mapped.
     * @return The mapped UserStatusDto.
     */
    public abstract UserStatusDto mapStatus(Optional<User> user);

    /**
     * Maps a User to a UserDto.
     *
     * @param user The User to be mapped.
     * @return The mapped UserDto.
     */
    @Mapping(target = "picName", expression = "java(user.getPicName() != null ? siteUrl + \"/user/getImage?picName=\" + user.getPicName() : null)")
    public abstract UserDto mapDto(User user);


    /**
     * Maps a list of User entities to a list of UserDto.
     *
     * @param userList The list of User entities to be mapped.
     * @return The mapped list of UserDto.
     */
    public abstract List<UserDto> userDtoList(List<User> userList);

    /**
     * Maps a User to a UserVerifyDto.
     *
     * @param user The User to be mapped.
     * @return The mapped UserVerifyDto.
     */
    public abstract UserVerifyDto userMap(User user);

}

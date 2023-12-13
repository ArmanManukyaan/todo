package com.example.todorest.dto;

import com.example.todorest.entity.UserType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private int id;
    private String name;
    private String surname;
    private String email;
    private boolean enabled;
    private String picName;
    @Enumerated(EnumType.STRING)
    private UserType userType;
}

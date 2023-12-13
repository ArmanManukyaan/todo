package com.example.todorest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVerifyDto {
    private String name;
    private String surname;
    private boolean enabled;

}

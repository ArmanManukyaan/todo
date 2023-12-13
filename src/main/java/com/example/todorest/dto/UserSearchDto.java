package com.example.todorest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSearchDto {
    private String name;
    private String surname;
    private String email;

    private String sortBy;
    private String sortDirection;
}

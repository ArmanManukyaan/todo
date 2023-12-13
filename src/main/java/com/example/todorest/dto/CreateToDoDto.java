package com.example.todorest.dto;

import com.example.todorest.entity.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateToDoDto {

    @NotBlank(message = "Title should not be empty")
    private String title;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Positive(message = "Field should be greater than zero")
    private int categoryId;


    private int userId;

}

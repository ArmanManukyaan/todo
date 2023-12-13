package com.example.todorest.dto;

import com.example.todorest.entity.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToDoDto {
    private int id;
    private String title;
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToOne
    private CategoryDto categoryDto;
    @ManyToOne
    private UserDto userDto;
}
package com.example.todorest.dto;

import com.example.todorest.entity.Status;
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
public class ToDoUpdateDto {
    @Enumerated(EnumType.STRING)
    private Status status;
}
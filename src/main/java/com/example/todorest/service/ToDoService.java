package com.example.todorest.service;

import com.example.todorest.dto.CreateToDoDto;
import com.example.todorest.dto.ToDoDto;
import com.example.todorest.dto.ToDoUpdateDto;
import com.example.todorest.entity.Category;
import com.example.todorest.entity.Status;
import com.example.todorest.entity.Todo;
import com.example.todorest.security.CurrentUser;

import java.util.List;
import java.util.Optional;

public interface ToDoService {

    Todo save(CurrentUser currentUser, CreateToDoDto createToDoDto);

    List<ToDoDto> todoList(CurrentUser currentUser);

    List<ToDoDto> todoListByStatus(Status status, int userId);

    List<ToDoDto> todoListBySCategory(Category category, int userId);

    ToDoDto update(ToDoUpdateDto updateDto, int id);

    Optional<Todo> deleteByIdTodo(int id, CurrentUser currentUser);


}

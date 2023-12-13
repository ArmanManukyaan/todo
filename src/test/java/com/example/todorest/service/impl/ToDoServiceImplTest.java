package com.example.todorest.service.impl;

import com.example.todorest.dto.*;
import com.example.todorest.entity.*;
import com.example.todorest.exception.ServiceImplNotFundException;
import com.example.todorest.mapper.ToDoMapper;
import com.example.todorest.repository.CategoryRepository;
import com.example.todorest.repository.ToDoRepository;
import com.example.todorest.security.CurrentUser;
import com.example.todorest.service.ToDoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ToDoServiceImplTest {
    private final CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);
    private final ToDoRepository toDoRepository = Mockito.mock(ToDoRepository.class);
    private final ToDoMapper toDoMapper = Mockito.mock(ToDoMapper.class);
    private final CurrentUser currentUser = Mockito.mock(CurrentUser.class);
    private ToDoService toDoService;

    @BeforeEach
    void setUp() {
        toDoService = new ToDoServiceImpl(toDoRepository, categoryRepository, toDoMapper);
    }

    @Test
    void save() {
        Todo todo = getTodo();
        CurrentUser currentUser_test = new CurrentUser(getUser());
        CreateToDoDto toDoDto = CreateToDoDto.builder()
                .title(todo.getTitle())
                .status(todo.getStatus())
                .build();
        when(categoryRepository.findById(toDoDto.getCategoryId())).thenReturn(Optional.of(getCategory()));
        when(toDoMapper.map(toDoDto)).thenReturn(todo);
        when(toDoRepository.save(todo)).thenReturn(todo);
        Todo result = toDoService.save(currentUser_test, toDoDto);
        Assertions.assertEquals(result, todo);
    }


    @Test
    void saveThrowsException() {
        User user = getUser();
        CreateToDoDto createToDoDto = new CreateToDoDto();
        createToDoDto.setCategoryId(user.getId());
        when(categoryRepository.findById(createToDoDto.getCategoryId())).thenReturn(Optional.empty());
        assertThrows(ServiceImplNotFundException.class, () -> {
            toDoService.save(currentUser, createToDoDto);
        });
    }

    @Test
    void todoList() {
        User user = getUser();
        Todo todo = getTodo();
        ToDoDto toDoDto = getToDoDto();
        List<Todo> todoList = List.of(todo);

        when(currentUser.getUser()).thenReturn(user);
        when(toDoRepository.findTodoByUserId(user.getId())).thenReturn(todoList);
        when(toDoMapper.toDoDtoList(todoList)).thenReturn(List.of(toDoDto));

        List<ToDoDto> result = toDoService.todoList(currentUser);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.size(), todoList.size());
    }

    @Test
    void todoListThrowsException() {
        when(currentUser.getUser()).thenReturn(null);
        assertThrows(ServiceImplNotFundException.class, () -> {
            toDoService.todoList(currentUser);
        });
    }

    @Test
    void todoListByStatus() {
        User user = getUser();
        Status status = Status.NOT_STARTED;
        Todo todo = getTodo();
        ToDoDto toDoDto = getToDoDto();

        List<Todo> todoList = List.of(todo);
        when(currentUser.getUser()).thenReturn(user);
        when(toDoRepository.findTodoByStatusAndUserId(status, user.getId())).thenReturn(todoList);
        when(toDoMapper.toDoDtoList(todoList)).thenReturn(List.of(toDoDto));

        List<ToDoDto> result = toDoService.todoListByStatus(status, user.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.size(), todoList.size());

    }

    @Test
    void todoListByStatusThrowsException() {
        User user = getUser();
        Status status = Status.NOT_STARTED;
        when(toDoRepository.findTodoByStatusAndUserId(status, user.getId())).thenReturn(Arrays.asList());
        assertThrows(ServiceImplNotFundException.class, () -> {
            toDoService.todoListByStatus(status, user.getId());
        });
    }

    @Test
    void todoListBySCategory() {
        User user = getUser();
        Todo todo = getTodo();
        ToDoDto toDoDto = getToDoDto();
        List<Todo> todoList = List.of(todo);
        when(currentUser.getUser()).thenReturn(user);
        when(toDoRepository.findTodoByCategoryAndUserId(getCategory(), user.getId())).thenReturn(todoList);
        when(toDoMapper.toDoDtoList(todoList)).thenReturn(List.of(toDoDto));
        List<ToDoDto> result = toDoService.todoListBySCategory(getCategory(), user.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.size(), todoList.size());
    }


    @Test
    void update() {
        int updateId = getUser().getId();
        ToDoUpdateDto toDoUpdateDto = new ToDoUpdateDto();
        toDoUpdateDto.setStatus(Status.DONE);

        Todo todo = new Todo();
        todo.setStatus(Status.NOT_STARTED);

        ToDoDto toDoDto = new ToDoDto();
        toDoDto.setStatus(toDoUpdateDto.getStatus());

        when(toDoRepository.findById(updateId)).thenReturn(Optional.of(todo));
        when(toDoRepository.save(todo)).thenReturn(todo);
        when(toDoMapper.mapDto(todo)).thenReturn(toDoDto);
        ToDoDto result = toDoService.update(toDoUpdateDto, updateId);

        Assertions.assertEquals(result, toDoDto);
        Assertions.assertEquals(result.getId(), toDoDto.getId());
    }

    @Test
    void updateTestNotFound() {
        int updateId = getUser().getId();
        ToDoUpdateDto updateDto = new ToDoUpdateDto();
        when(toDoRepository.findById(updateId)).thenReturn(Optional.empty());
        assertThrows(ServiceImplNotFundException.class, () -> {
            toDoService.update(updateDto, updateId);
        });
    }

    @Test
    void deleteByIdTodo() {

    }

    private User getUser() {
        return User
                .builder()
                .id(1)
                .name("Poxos")
                .password("2134656")
                .surname("Poxosyan")
                .email("poxos@mail.com")
                .userType(UserType.USER)
                .enabled(true)
                .build();
    }

    private UserDto getUserDto() {
        return UserDto.builder()
                .id(1)
                .name("Poxos")
                .surname("Poxosyan")
                .email("poxos@mail.com")
                .userType(UserType.USER)
                .enabled(true)
                .build();
    }

    private Category getCategory() {
        return Category.builder()
                .id(1)
                .name("Java")
                .build();
    }

    private CategoryDto getCategoryDto() {
        return CategoryDto.builder()
                .id(1)
                .name("category")
                .build();
    }

    private Todo getTodo() {
        return Todo.builder()
                .id(1)
                .title("Aaaa")
                .category(getCategory())
                .user(getUser())
                .status(Status.NOT_STARTED)
                .build();
    }

    private ToDoDto getToDoDto() {
        return ToDoDto.builder()
                .id(1)
                .title("Aaaa")
                .categoryDto(getCategoryDto())
                .userDto(getUserDto())
                .status(Status.NOT_STARTED)
                .build();
    }

}
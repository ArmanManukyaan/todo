package com.example.todorest.endpoint;

import com.example.todorest.dto.CreateToDoDto;
import com.example.todorest.dto.ToDoDto;
import com.example.todorest.dto.ToDoUpdateDto;
import com.example.todorest.entity.Category;
import com.example.todorest.entity.Status;
import com.example.todorest.mapper.ToDoMapper;
import com.example.todorest.security.CurrentUser;
import com.example.todorest.service.ToDoService;
import com.example.todorest.validation.ValidationCheck;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/todo")
public class ToDoEndpoint {
    private final ToDoService toDoService;
    private final ToDoMapper toDoMapper;
    @PostMapping
    public ResponseEntity<?> addToDo(@AuthenticationPrincipal CurrentUser currentUser,
                                     @RequestBody @Valid CreateToDoDto createToDoDto, BindingResult bindingResult) {
        StringBuilder validationResult = ValidationCheck.checkValidation(bindingResult);
        return !validationResult.isEmpty() ? ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body(validationResult.toString()) :
                ResponseEntity.ok(toDoMapper.mapDto(toDoService.save(currentUser, createToDoDto)));

    }

    @GetMapping
    public ResponseEntity<List<ToDoDto>> getTodoByUserId(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(toDoService.todoList(currentUser));
    }

    @GetMapping("/get")
    public ResponseEntity<List<ToDoDto>> getTodoByStatus(@RequestParam("byStatus") Status byStatus,
                                                   @AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(toDoService.todoListByStatus(byStatus, currentUser.getUser().getId()));
    }


    @GetMapping("/getCategoryId")
    public ResponseEntity<List<ToDoDto>> getTodoByCategory(@RequestParam("id") Category id,
                                                     @AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(toDoService.todoListBySCategory(id, currentUser.getUser().getId()));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ToDoDto> update(@RequestBody ToDoUpdateDto toDoUpdateDto,
                                          @PathVariable("id") int id) {
        return ResponseEntity.ok(toDoService.update(toDoUpdateDto, id));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@AuthenticationPrincipal CurrentUser currentUser,
                                        @PathVariable("id") int id) {
        return ResponseEntity.ok(toDoService.deleteByIdTodo(id, currentUser));

    }
}


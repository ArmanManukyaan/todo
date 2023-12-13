package com.example.todorest.repository;

import com.example.todorest.entity.Category;
import com.example.todorest.entity.Status;
import com.example.todorest.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ToDoRepository extends JpaRepository<Todo, Integer> {
    List<Todo> findTodoByUserId(int id);

    List<Todo> findTodoByStatusAndUserId(Status status, Integer id);

    List<Todo> findTodoByCategoryAndUserId(Category category, Integer id);
}

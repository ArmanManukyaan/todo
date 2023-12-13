package com.example.todorest.mapper;

import com.example.todorest.dto.CreateToDoDto;
import com.example.todorest.dto.ToDoDto;
import com.example.todorest.entity.Category;
import com.example.todorest.entity.Todo;
import com.example.todorest.entity.User;
import com.example.todorest.repository.CategoryRepository;
import com.example.todorest.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Mapper class for mapping between CreateToDoDto, Todo, ToDoDto, Category, and User entities.
 */
@Mapper(componentModel = "spring", uses = {CategoryMapper.class, UserMapper.class})
public abstract class ToDoMapper {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;


    /**
     * Maps a CreateToDoDto to a Todo.
     *
     * @param dto The CreateToDoDto to be mapped.
     * @return The mapped Todo.
     */
    @Mapping(target = "category", source = "categoryId", qualifiedByName = "categoryById")
    @Mapping(target = "user", source = "userId", qualifiedByName = "userById")
    public abstract Todo map(CreateToDoDto dto);


    /**
     * Maps a Todo to a ToDoDto.
     *
     * @param entity The Todo to be mapped.
     * @return The mapped ToDoDto.
     */
    @Mapping(target = "categoryDto", source = "category")
    @Mapping(target = "userDto", source = "user")
    public abstract ToDoDto mapDto(Todo entity);


    /**
     * Maps a list of Todo entities to a list of ToDoDto.
     *
     * @param todoList The list of Todo entities to be mapped.
     * @return The mapped list of ToDoDto.
     */
    public abstract List<ToDoDto> toDoDtoList(List<Todo> todoList);


    /**
     * Retrieves a Category entity by its ID.
     *
     * @param id The ID of the Category to be retrieved.
     * @return The Category entity if found, or null if not found.
     */
    @Named("categoryById")
    protected Category categoryById(int id) {
        return categoryRepository.findById(id).orElse(null);
    }

    /**
     * Retrieves a User entity by its ID.
     *
     * @param id The ID of the User to be retrieved.
     * @return The User entity if found, or null if not found.
     */
    @Named("userById")
    protected User userById(int id) {
        return userRepository.findById(id).orElse(null);
    }

}

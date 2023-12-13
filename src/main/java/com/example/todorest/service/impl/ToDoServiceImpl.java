package com.example.todorest.service.impl;

import com.example.todorest.dto.CreateToDoDto;
import com.example.todorest.dto.ToDoDto;
import com.example.todorest.dto.ToDoUpdateDto;
import com.example.todorest.entity.Category;
import com.example.todorest.entity.Status;
import com.example.todorest.entity.Todo;
import com.example.todorest.entity.User;
import com.example.todorest.exception.ServiceImplConflictException;
import com.example.todorest.exception.ServiceImplNotFundException;
import com.example.todorest.mapper.ToDoMapper;
import com.example.todorest.repository.CategoryRepository;
import com.example.todorest.repository.ToDoRepository;
import com.example.todorest.security.CurrentUser;
import com.example.todorest.service.ToDoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ToDoServiceImpl implements ToDoService {
    private final ToDoRepository toDoRepository;
    private final CategoryRepository categoryRepository;
    private final ToDoMapper toDoMapper;


    /**
     * Saves a new Todo item for the current user based on the provided data.
     *
     * @param currentUser   The currently logged-in user.
     * @param createToDoDto The data for creating a new Todo item.
     * @return Todo representing the saved Todo item.
     * @throws ServiceImplNotFundException If the specified category ID is not found.
     */
    @Override
    public Todo save(CurrentUser currentUser, CreateToDoDto createToDoDto) {
        User user = currentUser.getUser();
        Optional<Category> byId = categoryRepository.findById(createToDoDto.getCategoryId());
        if (byId.isEmpty()) {
            throw new ServiceImplNotFundException("Category by id is empty");
        } else {
            createToDoDto.setCategoryId(byId.get().getId());
            createToDoDto.setStatus(Status.NOT_STARTED);
            createToDoDto.setUserId(user.getId());
            Todo todo = toDoMapper.map(createToDoDto);
            log.info("Todo has been successfully added");
            return toDoRepository.save(todo);
        }
    }


    /**
     * Retrieves the list of Todo items for the current user.
     *
     * @param currentUser The currently logged-in user.
     * @return List of ToDoDto representing the Todo items for the user.
     * @throws ServiceImplNotFundException If the current user is empty.
     */

    @Override
    public List<ToDoDto> todoList(CurrentUser currentUser) {
        User user = currentUser.getUser();
        if (user == null) {
            throw new ServiceImplNotFundException("User is empty");
        } else {
            List<Todo> todoUserList = toDoRepository.findTodoByUserId(user.getId());
            log.info("todoList() in UserServiceImpl has successfully worked");
            return toDoMapper.toDoDtoList(todoUserList);
        }
    }


    /**
     * Retrieves the list of Todo items for a specific user and status.
     *
     * @param byStatus The status of Todo items to retrieve.
     * @param userId   The unique identifier of the user.
     * @return List of ToDoDto representing the Todo items for the user with the specified status.
     * @throws ServiceImplNotFundException If the Todo list with the specified status is empty.
     */

    @Override
    public List<ToDoDto> todoListByStatus(Status byStatus, int userId) {
        List<Todo> todoByStatus = toDoRepository.findTodoByStatusAndUserId(byStatus, userId);
        if (todoByStatus.isEmpty()) {
            throw new ServiceImplNotFundException("ToDo By status is empty");
        } else {
            log.info("todoListByStatus() in UserServiceImpl has successfully worked");
            return toDoMapper.toDoDtoList(todoByStatus);
        }
    }

    /**
     * Retrieves the list of Todo items for a specific user and category.
     *
     * @param category The category of Todo items to retrieve.
     * @param userId   The unique identifier of the user.
     * @return List of ToDoDto representing the Todo items for the user with the specified category.
     * @throws ServiceImplNotFundException If the Todo list with the specified category is empty.
     */
    @Override
    public List<ToDoDto> todoListBySCategory(Category category, int userId) {
        List<Todo> todoByCategoryAndUserId = toDoRepository.findTodoByCategoryAndUserId(category, userId);
        if (todoByCategoryAndUserId.isEmpty()) {
            throw new ServiceImplNotFundException("ToDo by categoryId is empty");
        } else {
            log.info("todoListByCategory() in UserServiceImpl has successfully worked");
            return toDoMapper.toDoDtoList(todoByCategoryAndUserId);
        }
    }

    /**
     * Updates the status of a Todo item based on the provided updateDto and Todo ID.
     *
     * @param updateDto The data for updating the Todo item.
     * @param id        The unique identifier of the Todo item to be updated.
     * @return ToDoDto representing the updated Todo item.
     * @throws ServiceImplNotFundException If the specified Todo ID is not found.
     */

    @Override
    public ToDoDto update(ToDoUpdateDto updateDto, int id) {
        Optional<Todo> byId = toDoRepository.findById(id);
        if (byId.isEmpty()) {
            throw new ServiceImplNotFundException("Todo by id is empty");
        } else {
            Todo todo = byId.get();
            todo.setStatus(updateDto.getStatus());
            Todo save = toDoRepository.save(todo);
            log.info("Todo with the " + todo.getId() + " id was updated");
            return toDoMapper.mapDto(save);
        }
    }

    /**
     * Deletes a Todo item by its ID for the current user.
     *
     * @param id          The unique identifier of the Todo item to be deleted.
     * @param currentUser The currently logged-in user.
     * @return Optional containing the deleted Todo item, or an empty Optional if not found.
     * @throws ServiceImplConflictException If there is a conflict with the user ID or the specified Todo ID.
     */
    @Override
    public Optional<Todo> deleteByIdTodo(int id, CurrentUser currentUser) {
        int idUser = currentUser.getUser().getId();
        if (idUser == 0) {
            throw new ServiceImplConflictException("User Id is equal to you 0");
        } else {
            Optional<Todo> byId = toDoRepository.findById(id);
            if (byId.get().getUser().getId() != idUser) {
                throw new ServiceImplConflictException("The user id is not the same as the id taken in the parameter");
            } else {
                log.info("Todo with the " + id + " id was deleted");
                toDoRepository.deleteById(byId.get().getId());
                return byId;
            }
        }
    }
}

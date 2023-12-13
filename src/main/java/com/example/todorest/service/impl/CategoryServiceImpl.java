package com.example.todorest.service.impl;

import com.example.todorest.dto.CategoryDto;
import com.example.todorest.dto.CategoryUpdateDto;
import com.example.todorest.entity.Category;
import com.example.todorest.exception.ServiceImplNotFundException;
import com.example.todorest.mapper.CategoryMapper;
import com.example.todorest.repository.CategoryRepository;
import com.example.todorest.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    /**
     * Saves a new Category.
     *
     * @param category The Category to be saved.
     * @return CategoryDto representing the saved Category.
     */

    @Override
    public CategoryDto save(Category category) {
        Category save = categoryRepository.save(category);
        log.info("Category has been successfully added");
        return categoryMapper.mapDto(save);
    }

    /**
     * Retrieves a list of all Categories.
     *
     * @return List of CategoryDto representing all Categories.
     * @throws ServiceImplNotFundException If the Category list is empty.
     */

    @Override
    public List<CategoryDto> categoryList() {
        List<Category> all = categoryRepository.findAll();
        if (all.isEmpty()) {
            throw new ServiceImplNotFundException("Category list by empty");
        }
        return categoryMapper.categoryListMap(all);
    }

    /**
     * Retrieves a Category by its unique identifier.
     *
     * @param id The unique identifier of the Category to retrieve.
     * @return Optional containing the Category if found, or an empty Optional if not found.
     * @throws ServiceImplNotFundException If the Category with the specified ID is not found.
     */
    @Override
    public Optional<Category> categoryById(int id) {
        Optional<Category> byId = categoryRepository.findById(id);
        if (byId.isEmpty()) {
            throw new ServiceImplNotFundException("Category By Id Is empty");
        }
        return byId;
    }

    /**
     * Updates a Category based on the provided updateDto and Category ID.
     *
     * @param id        The unique identifier of the Category to be updated.
     * @param updateDto The data for updating the Category.
     * @return Optional containing the updated CategoryDto if found, or an empty Optional if not found.
     * @throws ServiceImplNotFundException If the Category with the specified ID is not found.
     */
    @Override
    public Optional<CategoryDto> update(int id, CategoryUpdateDto updateDto) {
        Optional<Category> byId = categoryRepository.findById(id);
        if (byId.isEmpty()) {
            throw new ServiceImplNotFundException("Category By Id is empty");
        } else {
            Category category = byId.get();
            category.setName(updateDto.getName());
            CategoryDto categoryDto = categoryMapper.mapDto(categoryRepository.save(category));
            log.info("Category with the " + category.getId() + " id was updated");
            return Optional.of(categoryDto);
        }
    }

    /**
     * Checks if a Category with the specified ID exists.
     *
     * @param id The unique identifier of the Category to check for existence.
     * @return true if the Category exists, false otherwise.
     * @throws ServiceImplNotFundException If the Category with the specified ID is not found.
     */
    @Override
    public boolean existsById(int id) {
        boolean exists = categoryRepository.existsById(id);
        if (!exists) {
            throw new ServiceImplNotFundException("Category By Id is empty");
        }
        return true;
    }

    /**
     * Deletes a Category by its unique identifier.
     *
     * @param id The unique identifier of the Category to be deleted.
     */
    @Override
    public void deleteById(int id) {
        categoryRepository.deleteById(id);
    }
}



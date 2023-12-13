package com.example.todorest.service;

import com.example.todorest.dto.CategoryDto;
import com.example.todorest.dto.CategoryUpdateDto;
import com.example.todorest.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    CategoryDto save(Category category);

    List<CategoryDto> categoryList();

    Optional<Category> categoryById(int id);

    Optional<CategoryDto> update(int id, CategoryUpdateDto categoryUpdateDto);

    boolean existsById(int id);

    void deleteById(int id);

}

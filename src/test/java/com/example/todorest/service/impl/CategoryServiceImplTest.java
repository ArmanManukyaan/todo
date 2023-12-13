package com.example.todorest.service.impl;
import com.example.todorest.dto.CategoryDto;
import com.example.todorest.dto.CategoryUpdateDto;
import com.example.todorest.entity.Category;
import com.example.todorest.exception.ServiceImplNotFundException;
import com.example.todorest.mapper.CategoryMapper;
import com.example.todorest.repository.CategoryRepository;
import com.example.todorest.service.CategoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    private final CategoryRepository repository = Mockito.mock(CategoryRepository.class);
    private final CategoryMapper mapper = Mockito.mock(CategoryMapper.class);
    private final ServiceImplNotFundException serviceImplNotFundException = Mockito.mock(ServiceImplNotFundException.class);
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(mapper, repository);
    }

    @Test
    void save() {
        Category category = new Category(1, "category");
        CategoryDto categoryDto = new CategoryDto(1, "category");
        when(repository.save(category)).thenReturn(category);
        when(mapper.mapDto(category)).thenReturn(categoryDto);
        CategoryDto result = categoryService.save(category);
        Assertions.assertEquals(result.getId(), category.getId());
        verify(repository, times(1)).save(category);
    }


    @Test
    void categoryList() {
        Category category = new Category(1, "category");
        CategoryDto categoryDto = new CategoryDto(1, "category");
        List<Category> list = List.of(category);
        when(repository.findAll()).thenReturn(list);
        when(mapper.categoryListMap(list)).thenReturn(List.of(categoryDto));
        List<CategoryDto> categoryDt = categoryService.categoryList();
        Assertions.assertEquals(categoryDt.size(), list.size());
    }


    @Test
    void categoryListNotFund() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        Assertions.assertThrows(ServiceImplNotFundException.class, () -> categoryService.categoryList());
        verify(repository, times(1)).findAll();
    }


    @Test
    void categoryById() {
        int categoryId = 1;
        Category existingCategory = new Category(categoryId, "category");
        when(repository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        Optional<Category> result = categoryService.categoryById(categoryId);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(existingCategory, result.get());
    }

    @Test
    void testCategoryByIdNotFound() {
        int categoryId = 1;
        when(repository.findById(categoryId)).thenReturn(Optional.empty());
        assertThrows(ServiceImplNotFundException.class, () -> categoryService.categoryById(categoryId));
        verify(repository, times(1)).findById(categoryId);
    }

    @Test
    void update() {
        int categoryId = 1;
        CategoryUpdateDto updateDto = new CategoryUpdateDto();
        updateDto.setName("Java");

        Category existCategory = new Category();
        existCategory.setId(categoryId);
        existCategory.setName("OldCategory");

        Category updateCategory = new Category();
        updateCategory.setId(categoryId);
        updateCategory.setName(updateDto.getName());

        CategoryDto updatedCategoryDto = new CategoryDto();
        updatedCategoryDto.setId(categoryId);
        updatedCategoryDto.setName(updateDto.getName());

        when(repository.findById(categoryId))
                .thenReturn(Optional.of(existCategory));

        when(repository.save(updateCategory))
                .thenReturn(updateCategory);

        when(mapper.mapDto(updateCategory))
                .thenReturn(updatedCategoryDto);

        Optional<CategoryDto> result = categoryService.update(categoryId, updateDto);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(result.get().getId(), categoryId);
        Assertions.assertEquals(result.get().getName(), updateDto.getName());
    }

    @Test
    void updateCategoryNotFundTest() {
        int updateId = 1;
        CategoryUpdateDto categoryUpdateDto = new CategoryUpdateDto();
        categoryUpdateDto.setName("Spring");
        when(repository.findById(updateId)).thenReturn(Optional.empty());
        Assertions.assertThrows(ServiceImplNotFundException.class, ()
                -> categoryService.update(updateId, categoryUpdateDto));
        verify(repository, times(1)).findById(updateId);
        verify(repository, never()).save(any(Category.class));

    }


    @Test
    void existsById() {
        int id = 1;
        when(repository.existsById(id)).thenReturn(Boolean.TRUE);
        boolean result = categoryService.existsById(id);
        Assertions.assertTrue(true);
        verify(repository, times(1)).existsById(id);
    }


    @Test
    void testNotExistsById() {
        int categoryId = 1;
        when(repository.existsById(categoryId)).thenReturn(false);
        assertThrows(ServiceImplNotFundException.class, () -> categoryService.existsById(categoryId));
        verify(repository, times(1)).existsById(categoryId);
    }
}
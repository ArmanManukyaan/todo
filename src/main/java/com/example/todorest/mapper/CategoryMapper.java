package com.example.todorest.mapper;

import com.example.todorest.dto.CategoryDto;
import com.example.todorest.entity.Category;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper interface for mapping between Category and CategoryDto.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {
    /**
     * Maps a CategoryDto to a Category.
     *
     * @param dto The CategoryDto to be mapped.
     * @return The mapped Category.
     */
    Category map(CategoryDto dto);

    /**
     * Maps a Category to a CategoryDto.
     *
     * @param entity The Category to be mapped.
     * @return The mapped CategoryDto.
     */
    CategoryDto mapDto(Category entity);

    /**
     * Maps a list of Category entities to a list of CategoryDto.
     *
     * @param categoryList The list of Category entities to be mapped.
     * @return The mapped list of CategoryDto.
     */
    List<CategoryDto> categoryListMap(List<Category> categoryList);

}

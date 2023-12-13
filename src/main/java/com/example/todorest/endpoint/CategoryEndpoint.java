package com.example.todorest.endpoint;

import com.example.todorest.dto.CategoryDto;
import com.example.todorest.dto.CategoryUpdateDto;
import com.example.todorest.entity.Category;
import com.example.todorest.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryEndpoint {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> addCategory(@RequestBody Category category) {
        return ResponseEntity.ok(categoryService.save(category));
    }


    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategoryList() {
        return ResponseEntity.ok(categoryService.categoryList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateDto(@PathVariable("id") int id
            , @RequestBody CategoryUpdateDto updateDto) {
        return categoryService.update(id, updateDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") int id) {
        if (categoryService.existsById(id)) {
            categoryService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}


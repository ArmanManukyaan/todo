package com.example.todorest.endpoint;

import com.example.todorest.config.SpringSecurityWebAuxTestConfig;
import com.example.todorest.dto.CategoryUpdateDto;
import com.example.todorest.entity.Category;
import com.example.todorest.entity.User;
import com.example.todorest.entity.UserType;
import com.example.todorest.repository.CategoryRepository;
import com.example.todorest.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(SpringSecurityWebAuxTestConfig.class)
class CategoryEndpointTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    public void after() {
        userRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @WithUserDetails("armanmanukyan@mail.com")
    void addCategory() throws Exception {
        createUser();
        Category category = Category.builder()
                .id(1)
                .name("Spring")
                .build();
        mvc.perform(MockMvcRequestBuilders.post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Spring"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
        Optional<Category> byId = categoryRepository.findById(category.getId());
    }


    @Test
    @WithUserDetails("armanmanukyan@mail.com")
    void getCategoryList() throws Exception {
        createUser();
        Category category1 = new Category(1, "one");
        Category category2 = new Category(2, "two");
        categoryRepository.saveAll(List.of(category1, category2));
        mvc.perform(MockMvcRequestBuilders.get("/category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value("one"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].name").value("two"));
    }


    @Test
    @WithUserDetails("armanmanukyan@mail.com")
    void updateDto() throws Exception {
        Category category = categoryRepository.save(Category.builder()
                .id(38)
                .name("one")
                .build());
        CategoryUpdateDto updateDto = new CategoryUpdateDto();
        updateDto.setName("two");
        Optional<Category> byId = categoryRepository.findById(category.getId());
        Assertions.assertTrue(byId.isPresent());
        mvc.perform(MockMvcRequestBuilders.put("/category/" + byId.get().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("two"));
    }


    @Test
    @WithUserDetails("armanmanukyan@mail.com")
    void deleteById() throws Exception {
        createUser();
        Category category = categoryRepository.save(Category.builder()
                .name("Java")
                .build());
        Optional<Category> byId = categoryRepository.findById(category.getId());
        Assertions.assertTrue(byId.isPresent());
        mvc.perform(MockMvcRequestBuilders.delete("/category/" + category.getId()))
                .andExpect(status().is(204));
        Assertions.assertTrue(categoryRepository.findById(category.getId()).isEmpty());
    }

    private void createUser() {
        userRepository.save(User.builder()
                .id(1)
                .email("armanmanukyan@mail.com")
                .name("poxos")
                .surname("poxosyan")
                .password("poxosi")
                .token(null)
                .enabled(true)
                .userType(UserType.ADMIN).build());
    }
}

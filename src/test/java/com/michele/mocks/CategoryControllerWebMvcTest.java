package com.michele.mocks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.michele.mocks.config.SecurityConfig;
import com.michele.mocks.controller.CategoryController;
import com.michele.mocks.dto.PageResponse;
import com.michele.mocks.dto.categories.CategoryResponse;
import com.michele.mocks.dto.categories.CategoryTreeResponse;
import com.michele.mocks.dto.categories.CreateCategoryRequest;
import com.michele.mocks.exception.ResourceNotFoundException;
import com.michele.mocks.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryController.class)
@Import(SecurityConfig.class)
class CategoryControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @Test
    void createCategoryValid() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("ELEC", "Electronics", "all electronics", null, null);
        CategoryResponse response = new CategoryResponse(
                1L, "ELEC", "Electronics", "all electronics", null, null, LocalDateTime.now(), LocalDateTime.now());

        when(categoryService.create(any(CreateCategoryRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("ELEC"));
    }

    @Test
    void createCategoryInvalidReturnsStructured400() throws Exception {
        String invalidPayload = """
                {
                  "code": "",
                  "name": ""
                }
                """;

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/api/v1/categories"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[0]", containsString(":")));
    }

    @Test
    void getCategoryNotFoundReturnsStructured404() throws Exception {
        when(categoryService.getCategory(404L))
                .thenThrow(new ResourceNotFoundException("Category not found: id=404"));

        mockMvc.perform(get("/api/v1/categories/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Category not found: id=404"))
                .andExpect(jsonPath("$.path").value("/api/v1/categories/404"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void getCategoriesPaginatedList() throws Exception {
        PageResponse<CategoryResponse> response = new PageResponse<>(
                List.of(new CategoryResponse(1L, "ELEC", "Electronics", "desc", null, null, null, null)),
                0,
                20,
                1,
                1,
                false,
                false,
                "name: ASC");

        when(categoryService.getAll(any(), any(), any(), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/categories")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.data[0].code").value("ELEC"));
    }

    @Test
    void getCategoryTree() throws Exception {
        CategoryTreeResponse response = new CategoryTreeResponse(
                1L,
                "ROOT",
                "Root",
                "root node",
                List.of(new CategoryTreeResponse(2L, "CHILD", "Child", "child node", List.of())));

        when(categoryService.getCategoryTree(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/categories/1/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.children[0].id").value(2))
                .andExpect(jsonPath("$.children[0].code").value("CHILD"));
    }
}

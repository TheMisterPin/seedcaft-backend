package com.michele.mocks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.michele.mocks.controller.ProductController;
import com.michele.mocks.dto.products.CreateProductRequest;
import com.michele.mocks.dto.products.ProductResponse;
import com.michele.mocks.exception.ResourceNotFoundException;
import com.michele.mocks.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
@Import(com.michele.mocks.config.SecurityConfig.class)
class ProductControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    void createProductValid() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
                "SKU-1", "iPhone", "smartphone", "111", 10L,
                "SERIAL", "EA", 1.0, 1.0, 1.0, 1.0, 1,
                "https://img", 100.0, 75.0, "USD");

        ProductResponse response = new ProductResponse(
                1L, "SKU-1", "iPhone", "smartphone", "111", "10", "https://img", 100.0, 75.0, "USD");

        when(productService.create(any(CreateProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sku").value("SKU-1"))
                .andExpect(jsonPath("$.currency").value("USD"));
    }

    @Test
    void createProductInvalidReturnsStructured400() throws Exception {
        String invalidPayload = """
                {
                  "sku": "",
                  "name": "",
                  "currency": "US"
                }
                """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/api/v1/products"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[0]", containsString(":")));
    }

    @Test
    void getProductNotFoundReturnsStructured404() throws Exception {
        when(productService.getProduct(404L))
                .thenThrow(new ResourceNotFoundException("Product not found: id=404"));

        mockMvc.perform(get("/api/v1/products/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Product not found: id=404"))
                .andExpect(jsonPath("$.path").value("/api/v1/products/404"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void getProductsPaginatedSortedFilteredAndSearched() throws Exception {
        PageRequest expectedPageable = PageRequest.of(1, 1, org.springframework.data.domain.Sort.by("name").descending());
        ProductResponse response = new ProductResponse(
                9L, "SKU-9", "Phone Case", "case", "999", "5", null, 15.0, 8.0, "USD");
        Page<ProductResponse> page = new PageImpl<>(List.of(response), expectedPageable, 3);

        when(productService.getAll(
                eq("phone"),
                eq(5L),
                eq("ELEC"),
                eq(new BigDecimal("10")),
                eq(new BigDecimal("20")),
                eq("USD"),
                any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/products")
                        .param("q", "phone")
                        .param("categoryId", "5")
                        .param("categoryCode", "ELEC")
                        .param("minPrice", "10")
                        .param("maxPrice", "20")
                        .param("currency", "USD")
                        .param("page", "1")
                        .param("size", "1")
                        .param("sort", "name,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(9))
                .andExpect(jsonPath("$.content[0].name").value("Phone Case"))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(3));

        verify(productService).getAll(
                eq("phone"),
                eq(5L),
                eq("ELEC"),
                eq(new BigDecimal("10")),
                eq(new BigDecimal("20")),
                eq("USD"),
                argThat(hasPagingAndSort(1, 1, "name")));
    }

    private static ArgumentMatcher<Pageable> hasPagingAndSort(int page, int size, String sortProperty) {
        return p -> p.getPageNumber() == page
                && p.getPageSize() == size
                && p.getSort().getOrderFor(sortProperty) != null
                && p.getSort().getOrderFor(sortProperty).isDescending();
    }
}

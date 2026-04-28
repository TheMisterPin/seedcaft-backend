package com.michele.mocks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.michele.mocks.config.SecurityConfig;
import com.michele.mocks.controller.ProductController;
import com.michele.mocks.dto.PageResponse;
import com.michele.mocks.dto.products.CreateProductRequest;
import com.michele.mocks.dto.products.ProductResponse;
import com.michele.mocks.exception.ResourceNotFoundException;
import com.michele.mocks.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
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

@WebMvcTest(controllers = ProductController.class)
@Import(SecurityConfig.class)
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
                "https://img", List.of("https://img/1"), new BigDecimal("100.00"), new BigDecimal("75.00"), "USD");

        ProductResponse response = new ProductResponse(
                1L, "SKU-1", "iPhone", "smartphone", "111", 10L, "ELEC", "Electronics",
                "SERIAL", "EA", 1.0, 1.0, 1.0, 1.0, 1,
                "https://img", List.of("https://img/1"), new BigDecimal("100.00"), new BigDecimal("75.00"), "USD",
                LocalDateTime.now(), LocalDateTime.now());

        when(productService.create(any(CreateProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(APPLICATION_JSON)
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
                        .contentType(APPLICATION_JSON)
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
        ProductResponse response = new ProductResponse(
                9L, "SKU-9", "Phone Case", "case", "999", 5L, "ELEC", "Electronics",
                null, null, 0.0, 0.0, 0.0, 0.0, 0,
                null, List.of(), new BigDecimal("15.00"), new BigDecimal("8.00"), "USD",
                LocalDateTime.now(), LocalDateTime.now());
        PageResponse<ProductResponse> page = new PageResponse<>(
                List.of(response), 1, 1, 3, 3, true, true, "name: DESC");

        when(productService.getAll(any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

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
                .andExpect(jsonPath("$.data[0].id").value(9))
                .andExpect(jsonPath("$.data[0].name").value("Phone Case"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(3));
    }
}

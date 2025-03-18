package bbt.tao.warehouse.controller;

import bbt.tao.warehouse.controller.api.ProductController;
import bbt.tao.warehouse.dto.product.ProductDTO;
import bbt.tao.warehouse.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(ProductControllerTest.TestConfig.class)
@EnableMethodSecurity
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductService productService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ProductService productService() {
            return mock(ProductService.class);
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(HttpMethod.GET, "/api/products/low-stock").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.POST, "/api/products").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                            .anyRequest().authenticated()
                    );
            return http.build();
        }
    }

    @Test
    public void whenAnonymous_thenGetActiveProductsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/products/active"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"WAREHOUSE_WORKER"})
    public void whenWarehouseWorker_thenGetActiveProductsOk() throws Exception {
        ProductDTO product1 = new ProductDTO();
        product1.setId(1L);
        product1.setName("Active Product");
        product1.setIsActive(true);
        when(productService.findAllActiveProducts()).thenReturn(List.of(product1));

        mockMvc.perform(get("/api/products/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Active Product")));

        verify(productService).findAllActiveProducts();
    }

    @Test
    @WithMockUser(roles = {"WAREHOUSE_WORKER"})
    public void getProductBySku() throws Exception {
        ProductDTO product = new ProductDTO();
        product.setId(1L);
        product.setSku("SKU123");

        when(productService.findProductBySku("SKU123")).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/sku/SKU123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku", is("SKU123")));

        verify(productService).findProductBySku("SKU123");
    }

    @Test
    @WithMockUser(roles = {"WAREHOUSE_WORKER"})
    public void getProductBySkuNotFound() throws Exception {
        when(productService.findProductBySku("NONEXISTENT")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/sku/NONEXISTENT"))
                .andExpect(status().isNotFound());

        verify(productService).findProductBySku("NONEXISTENT");
    }

    @Test
    @WithMockUser(roles = {"WAREHOUSE_WORKER"})
    public void whenWarehouseWorker_thenLowStockForbidden() throws Exception {
        mockMvc.perform(get("/api/products/low-stock").param("minStock", "5.0"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    public void whenManager_thenLowStockOk() throws Exception {
        ProductDTO product = new ProductDTO();
        product.setId(1L);
        product.setName("Low Stock");
        when(productService.findProductsWithLowStock(5.0)).thenReturn(List.of(product));

        mockMvc.perform(get("/api/products/low-stock").param("minStock", "5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Low Stock")));

        verify(productService).findProductsWithLowStock(5.0);
    }

    @Test
    @WithMockUser(roles = {"WAREHOUSE_WORKER"})
    public void whenWarehouseWorker_thenCreateProductForbidden() throws Exception {
        ProductDTO inputProduct = new ProductDTO();
        inputProduct.setName("New Product");
        inputProduct.setSku("NEWSKU");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputProduct)))
                .andExpect(status().isForbidden());

        verify(productService, never()).existsBySku("NEWSKU");
        verify(productService, never()).saveProduct(any());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    public void whenManager_thenCreateProductOk() throws Exception {
        ProductDTO inputProduct = new ProductDTO();
        inputProduct.setName("New Product");
        inputProduct.setSku("NEWSKU");

        when(productService.existsBySku("NEWSKU")).thenReturn(false);

        ProductDTO savedProduct = new ProductDTO();
        savedProduct.setId(100L);
        savedProduct.setName("New Product");
        savedProduct.setSku("NEWSKU");
        when(productService.saveProduct(any())).thenReturn(savedProduct);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(100)));

        verify(productService).existsBySku("NEWSKU");
        verify(productService).saveProduct(any());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    public void whenManager_thenUpdateProductNotFound() throws Exception {
        ProductDTO inputProduct = new ProductDTO();
        inputProduct.setName("Updated");

        when(productService.findProductById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputProduct)))
                .andExpect(status().isNotFound());

        verify(productService).findProductById(999L);
        verify(productService, never()).saveProduct(any());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    public void whenManager_thenUpdateProductWithConflictingSku() throws Exception {
        ProductDTO inputProduct = new ProductDTO();
        inputProduct.setName("Updated");
        inputProduct.setSku("CONFLICT");

        ProductDTO existingProduct = new ProductDTO();
        existingProduct.setId(1L);

        ProductDTO otherProduct = new ProductDTO();
        otherProduct.setId(2L);
        otherProduct.setSku("CONFLICT");

        when(productService.findProductById(1L)).thenReturn(Optional.of(existingProduct));
        when(productService.existsBySku("CONFLICT")).thenReturn(true);
        when(productService.findProductBySku("CONFLICT")).thenReturn(Optional.of(otherProduct));

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputProduct)))
                .andExpect(status().isConflict());

        verify(productService, never()).saveProduct(any());
    }

    @Test
    @WithMockUser(roles = {"WAREHOUSE_WORKER"})
    public void whenWarehouseWorker_thenDeleteProductForbidden() throws Exception {
        when(productService.findProductById(1L)).thenReturn(Optional.of(new ProductDTO()));

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isForbidden());

        verify(productService).findProductById(1L);
        verify(productService, never()).deleteProduct(any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void whenAdmin_thenDeleteProductOk() throws Exception {
        when(productService.findProductById(1L)).thenReturn(Optional.of(new ProductDTO()));
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService).findProductById(1L);
        verify(productService).deleteProduct(1L);
    }
}

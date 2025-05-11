package com.example.productsimilarityservice;

import com.example.productsimilarityservice.domain.model.Product;
import com.example.productsimilarityservice.domain.port.out.ProductDetailClient;
import com.example.productsimilarityservice.domain.port.out.SimilarIdsClient;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test end-to-end del ciclo de resilience:
 *   1) Tres fallos => Circuit open + fallback instantáneo
 *   2) Reset del CB => half-open probe => close y datos reales
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ResilienceE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SimilarIdsClient similarIdsClient;

    @MockBean
    private ProductDetailClient productDetailClient;

    @Autowired
    private CircuitBreakerRegistry cbRegistry;

    @BeforeEach
    void resetCircuitBreaker() {
        // Aseguramos que el CB está en estado CLOSED al inicio de cada test
        cbRegistry.circuitBreaker("productSimilarityService").reset();
        reset(similarIdsClient, productDetailClient);
    }

    @Test
    void whenSeveralFailures_thenFallbackInstantly() throws Exception {
        when(similarIdsClient.fetchSimilarIds("1"))
            .thenThrow(new RuntimeException("fail1"))
            .thenThrow(new RuntimeException("fail2"))
            .thenThrow(new RuntimeException("fail3"));
    
        // 3 peticiones, todas deben caer en fallback []
        for (int i = 1; i <= 3; i++) {
          mockMvc.perform(get("/product/1/similar"))
                 .andExpect(status().isOk())
                 .andExpect(content().json("[]"));
        }
    
        // Verifica que nunca se llegó a llamar al detail client
        verifyNoInteractions(productDetailClient);
    }

    @Test
    void whenRecovery_thenReturnRealData() throws Exception {
        // Stub de datos reales
        when(similarIdsClient.fetchSimilarIds("1"))
            .thenReturn(List.of("2","3"));
        Product p2 = new Product("2","Dress",19.99,true);
        Product p3 = new Product("3","Blazer",29.99,false);
        when(productDetailClient.fetchById("2")).thenReturn(p2);
        when(productDetailClient.fetchById("3")).thenReturn(p3);

        // Forzamos OPEN → HALF_OPEN
        var cb = cbRegistry.circuitBreaker("productSimilarityService");
        cb.transitionToOpenState();
        cb.transitionToHalfOpenState();

        // Este primer “probe half-open” debe invocar al use case real
        mockMvc.perform(get("/product/1/similar"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("2"))
            .andExpect(jsonPath("$[1].id").value("3"));

        // Comprobamos que el circuit se ha cerrado y las siguientes peticiones siguen devolviendo datos
        mockMvc.perform(get("/product/1/similar"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Dress"))
            .andExpect(jsonPath("$[1].name").value("Blazer"));
        }
}
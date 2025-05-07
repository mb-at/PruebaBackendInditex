package com.example.productsimilarityservice.adapter.in;


import com.example.productsimilarityservice.domain.model.Product;
import com.example.productsimilarityservice.domain.port.in.GetSimilarProducts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SimilarController.class)
class SimilarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetSimilarProducts getSimilarProducts; 

    @Test
    void getSimilar_returnsEmptyList() throws Exception {
        // Simulamos que no hay productos similares
        when(getSimilarProducts.execute("1"))
            .thenReturn(List.of());

        mockMvc.perform(get("/product/1/similar")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
    }

    @Test
    void getSimilar_returnsListOfProducts() throws Exception {
        // Creamos dos productos de ejemplo
        Product p2 = new Product("2", "Dress", 19.99, true);
        Product p3 = new Product("3", "Blazer", 29.99, false);
        when(getSimilarProducts.execute("1"))
            .thenReturn(List.of(p2, p3));

        mockMvc.perform(get("/product/1/similar")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("2"))
            .andExpect(jsonPath("$[0].name").value("Dress"))
            .andExpect(jsonPath("$[0].price").value(19.99))
            .andExpect(jsonPath("$[0].availability").value(true))
            .andExpect(jsonPath("$[1].id").value("3"))
            .andExpect(jsonPath("$[1].name").value("Blazer"))
            .andExpect(jsonPath("$[1].price").value(29.99))
            .andExpect(jsonPath("$[1].availability").value(false));
    }

}
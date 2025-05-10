package com.example.productsimilarityservice.application.service;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.example.productsimilarityservice.domain.model.Product;
import com.example.productsimilarityservice.domain.port.out.ProductDetailClient;
import com.example.productsimilarityservice.domain.port.out.SimilarIdsClient;

class GetSimilarProductsPartialTest {

    @Mock SimilarIdsClient similarIdsClient;
    @Mock ProductDetailClient productDetailClient;
    @InjectMocks GetSimilarProductsImpl useCase;

    @BeforeEach void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_ignoresSingleDetailFailure_andReturnsOthers() {
        // Stub de IDs similares
        when(similarIdsClient.fetchSimilarIds("X"))
            .thenReturn(Arrays.asList("A","B","C"));

        // Para A y C devuelvo producto, para B lanzo excepción
        Product pA = new Product("A","ItemA",10.0,true);
        Product pC = new Product("C","ItemC",30.0,false);
        when(productDetailClient.fetchById("A")).thenReturn(pA);
        when(productDetailClient.fetchById("B")).thenThrow(new RuntimeException("fail B"));
        when(productDetailClient.fetchById("C")).thenReturn(pC);

        List<Product> result = useCase.execute("X");

        // Comprueba que B se ha ignorado y A/C están
        assertThat(result).containsExactly(pA, pC);

        // Verifica llamadas
        InOrder inOrder = inOrder(productDetailClient);
        inOrder.verify(productDetailClient).fetchById("A");
        inOrder.verify(productDetailClient).fetchById("B");
        inOrder.verify(productDetailClient).fetchById("C");
    }
}
package com.example.productsimilarityservice.application.service;

import com.example.productsimilarityservice.domain.model.Product;
import com.example.productsimilarityservice.domain.port.out.ProductDetailClient;
import com.example.productsimilarityservice.domain.port.out.SimilarIdsClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GetSimilarProductsImplTest {
    
    
    @Mock SimilarIdsClient similarIdsClient;
    @Mock ProductDetailClient productDetailClient;
    @InjectMocks GetSimilarProductsImpl useCase;

    @BeforeEach void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test void execute_returnsEmptyList_whenNoSimilarIds() {
        when(similarIdsClient.fetchSimilarIds("1")).thenReturn(List.of());
        List<Product> result = useCase.execute("1");
        assertThat(result).isEmpty();
        verify(similarIdsClient).fetchSimilarIds("1");
        verifyNoMoreInteractions(productDetailClient);
    }

    @Test void execute_returnsListOfProducts_givenSimilarIds() {
        when(similarIdsClient.fetchSimilarIds("1"))
            .thenReturn(Arrays.asList("2", "3"));
        Product p2 = new Product("2", "Dress", 19.99, true);
        Product p3 = new Product("3", "Blazer", 29.99, false);
        when(productDetailClient.fetchById("2")).thenReturn(p2);
        when(productDetailClient.fetchById("3")).thenReturn(p3);

        List<Product> result = useCase.execute("1");

        assertThat(result).containsExactly(p2, p3);
        InOrder order = inOrder(similarIdsClient, productDetailClient);
        order.verify(similarIdsClient).fetchSimilarIds("1");
        order.verify(productDetailClient).fetchById("2");
        order.verify(productDetailClient).fetchById("3");
        verifyNoMoreInteractions(similarIdsClient, productDetailClient);
    }
}
package com.example.productsimilarityservice.application.service;

import com.example.productsimilarityservice.domain.model.Product;
import com.example.productsimilarityservice.domain.port.in.GetSimilarProducts;
import com.example.productsimilarityservice.domain.port.out.ProductDetailClient;
import com.example.productsimilarityservice.domain.port.out.SimilarIdsClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetSimilarProductsImpl implements GetSimilarProducts {

    private final SimilarIdsClient similarIdsClient;
    private final ProductDetailClient productDetailClient;

    public GetSimilarProductsImpl(SimilarIdsClient similarIdsClient,
                                  ProductDetailClient productDetailClient) {
        this.similarIdsClient = similarIdsClient;
        this.productDetailClient = productDetailClient;
    }

    @Override
    public List<Product> execute(String productId) {
        // 1. Obtener lista de IDs similares
        List<String> similarIds = similarIdsClient.fetchSimilarIds(productId);

        // 2. Por cada ID, obtener el detalle y mapear a domain.Product
        return similarIds.stream()
                .map(productDetailClient::fetchById)
                .collect(Collectors.toList());
    }
}

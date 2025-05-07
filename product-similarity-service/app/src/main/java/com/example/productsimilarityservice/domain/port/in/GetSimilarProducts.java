package com.example.productsimilarityservice.domain.port.in;

import com.example.productsimilarityservice.domain.model.Product;

import java.util.List;

/**
 * Port de entrada: orquesta la obtención de productos similares.
 */
public interface GetSimilarProducts {
    /**
     * Devuelve el detalle de los productos similares al dado.
     *
     * @param productId ID del producto original
     * @return lista de productos similares con detalle completo
     */
    List<Product> execute(String productId);
}
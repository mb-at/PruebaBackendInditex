package com.example.productsimilarityservice.domain.port.out;

import java.util.List;

/**
 * Port de salida: define la operación de fetch de IDs similares.
 */

public interface SimilarIdsClient {
    /**
     * Obtiene la lista de IDs de productos similares para un productId dado.
     * 
     * @param productId el ID del producto original
     * @return lista de IDs similares
     */
    List<String> fetchSimilarIds(String productId);
}
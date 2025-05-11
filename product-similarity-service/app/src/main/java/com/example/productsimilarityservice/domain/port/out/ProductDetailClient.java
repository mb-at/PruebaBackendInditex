package com.example.productsimilarityservice.domain.port.out;

import com.example.productsimilarityservice.domain.model.Product;

/**
 * Port de salida: define la operación de fetch de los detalles de
 * los productos
 */

public interface ProductDetailClient {
    /**
    * Obtiene el id del producto similar al original.
    * 
    * @param productId el ID del producto similar
    * @return Detalle de ese producto en concreto
    */
    Product fetchById(String productId);
}
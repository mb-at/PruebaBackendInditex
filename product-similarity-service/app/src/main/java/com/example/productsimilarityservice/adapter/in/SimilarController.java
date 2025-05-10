package com.example.productsimilarityservice.adapter.in;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.productsimilarityservice.domain.model.Product;
import com.example.productsimilarityservice.domain.port.in.GetSimilarProducts;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/product")
public class SimilarController {

    private final GetSimilarProducts getSimilarProducts;

    public SimilarController(GetSimilarProducts getSimilarProducts) {
        this.getSimilarProducts = getSimilarProducts;
    }

    @Operation(
        summary = "Obtener productos similares a un producto pasado por parámetro",
        responses = {
        @ApiResponse(responseCode = "200", description = "Listado de productos similares")
        }
    )
    @GetMapping("/{id}/similar")
    public ResponseEntity<List<Product>> getSimilar(@PathVariable("id") String productId) {
        List<Product> similars = getSimilarProducts.execute(productId);
        return ResponseEntity.ok(similars);
    }
}

package com.example.productsimilarityservice.adapter.in;

import com.example.productsimilarityservice.domain.model.Product;
import com.example.productsimilarityservice.domain.port.in.GetSimilarProducts;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class SimilarController {

    private final GetSimilarProducts getSimilarProducts;

    public SimilarController(GetSimilarProducts getSimilarProducts) {
        this.getSimilarProducts = getSimilarProducts;
    }

    @GetMapping("/{id}/similar")
    public ResponseEntity<List<Product>> getSimilar(@PathVariable("id") String productId) {
        List<Product> similars = getSimilarProducts.execute(productId);
        return ResponseEntity.ok(similars);
    }
}

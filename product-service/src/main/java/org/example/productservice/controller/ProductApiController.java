package org.example.productservice.controller;

import org.example.productservice.model.Product;
import org.example.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products/api")
public class ProductApiController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable int id) {
        return productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
    }

    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable int id, @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable int id) {
        productService.deleteProduct(id);
    }

    // ✅ Endpoint giảm tồn kho
    @PostMapping("/{id}/decrease")
    public Product decreaseQuantity(@PathVariable int id, @RequestParam int amount) {
        return productService.decreaseQuantity(id, amount);
    }
}

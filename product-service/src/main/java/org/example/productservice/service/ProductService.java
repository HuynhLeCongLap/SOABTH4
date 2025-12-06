package org.example.productservice.service;

import org.example.productservice.model.Product;
import org.example.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Optional<Product> getProductById(int id) {
        return repository.findById(id);
    }

    public Product createProduct(Product product) {
        return repository.save(product);
    }

    public Product updateProduct(int id, Product updatedProduct) {
        return repository.findById(id)
                .map(product -> {
                    product.setName(updatedProduct.getName());
                    product.setDescription(updatedProduct.getDescription());
                    product.setPrice(updatedProduct.getPrice());
                    product.setQuantity(updatedProduct.getQuantity());
                    return repository.save(product);
                })
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void deleteProduct(int id) {
        repository.deleteById(id);
    }

    // ✅ Giảm tồn kho
    @Transactional
    public Product decreaseQuantity(int id, int amount) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getQuantity() < amount) {
            throw new RuntimeException("Không đủ tồn kho cho sản phẩm: " + product.getName());
        }
        product.setQuantity(product.getQuantity() - amount);
        return repository.save(product);
    }
    
}

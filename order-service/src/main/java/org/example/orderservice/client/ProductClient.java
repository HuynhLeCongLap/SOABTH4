package org.example.orderservice.client;

import org.example.orderservice.dto.ProductDTO;
import org.example.orderservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    private static final String PRODUCT_API = "http://localhost:8081/products/api";

    // ==================================================
    // LẤY TẤT CẢ SẢN PHẨM
    // ==================================================
    public List<ProductDTO> getAllProducts() {

        String token = jwtUtil.generateToken("order-service");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<ProductDTO[]> response =
                restTemplate.exchange(
                        PRODUCT_API,
                        HttpMethod.GET,
                        entity,
                        ProductDTO[].class
                );

        return Arrays.asList(response.getBody());
    }

    // ==================================================
    // LẤY SẢN PHẨM THEO ID  ←  BẠN ĐANG THIẾU HÀM NÀY !!!
    // ==================================================
    public ProductDTO getProductById(Long id) {

        String token = jwtUtil.generateToken("order-service");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<ProductDTO> response =
                restTemplate.exchange(
                        PRODUCT_API + "/" + id,   // <--- URL GET PRODUCT BY ID
                        HttpMethod.GET,
                        entity,
                        ProductDTO.class
                );

        return response.getBody();
    }
}

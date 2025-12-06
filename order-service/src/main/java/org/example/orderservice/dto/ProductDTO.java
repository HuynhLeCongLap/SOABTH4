package org.example.orderservice.dto;

import lombok.Data;

public class ProductDTO {
    private Long id;
    private String name;
    private Double price; // ✅ thêm field price
    private Integer quantity;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; } // ✅ getter cho price
    public void setPrice(Double price) { this.price = price; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}

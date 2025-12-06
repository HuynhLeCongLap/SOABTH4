package org.example.orderservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    @Column(name = "unit_price", precision = 15, scale = 2, nullable = false)
    private Double unitPrice = 0.0;

    @Column(name = "total_price", precision = 15, scale = 2, nullable = false)
    private Double totalPrice = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ========================
    // Getters & Setters
    // ========================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) {
        this.quantity = (quantity != null) ? quantity : 0;
        recalcTotalPrice();
    }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = (unitPrice != null) ? unitPrice : 0.0;
        recalcTotalPrice();
    }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = (totalPrice != null) ? totalPrice : 0.0; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ========================
    // Utility method
    // ========================
    public void recalcTotalPrice() {
        this.totalPrice = (this.unitPrice != null && this.quantity != null)
                ? this.unitPrice * this.quantity
                : 0.0;
    }
}

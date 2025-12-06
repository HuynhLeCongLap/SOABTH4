package org.example.orderservice.dto;

import java.util.List;

public class OrderDTO {

    public static class OrderItemDTO {
        private Long productId;
        private Integer quantity;

        // getters & setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    public static class OrderCreateDTO {
        private String customerName;
        private String customerEmail;
        private List<OrderItemDTO> items;

        // getters & setters
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }

        public String getCustomerEmail() { return customerEmail; }
        public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

        public List<OrderItemDTO> getItems() { return items; }
        public void setItems(List<OrderItemDTO> items) { this.items = items; }
    }
}

package org.example.orderservice.service;

import org.example.orderservice.dto.OrderDTO.OrderCreateDTO;
import org.example.orderservice.dto.OrderDTO.OrderItemDTO;
import org.example.orderservice.model.Order;
import org.example.orderservice.model.OrderItem;
import org.example.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.example.orderservice.util.JwtUtil;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final HttpServletRequest request;

    @Autowired
    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate, HttpServletRequest request) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
        this.request = request;
    }
    @Autowired
private JwtUtil jwtUtil;


    /** Lấy danh sách tất cả đơn hàng */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /** Tìm đơn hàng theo ID */
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    /** Tạo đơn hàng mới từ Order entity */
    @Transactional
    public Order createOrder(Order order) {
        double totalAmount = 0;
        String authHeader = request.getHeader("Authorization");

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new RuntimeException("⚠️ Đơn hàng phải có ít nhất một sản phẩm!");
        }

        LocalDateTime now = LocalDateTime.now();

        for (OrderItem item : order.getItems()) {
            try {
                String productUrl = "http://localhost:8081/products/" + item.getProductId();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", authHeader);
                HttpEntity<Void> entity = new HttpEntity<>(headers);

                ResponseEntity<ProductResponse> response = restTemplate.exchange(
                        productUrl, HttpMethod.GET, entity, ProductResponse.class
                );

                ProductResponse product = response.getBody();
                if (product == null) {
                    throw new RuntimeException("❌ Không tìm thấy sản phẩm ID = " + item.getProductId());
                }

                if (product.getQuantity() < item.getQuantity()) {
                    throw new RuntimeException("❌ Sản phẩm '" + product.getName() + "' không đủ tồn kho!");
                }

                item.setProductName(product.getName());
                item.setUnitPrice(product.getPrice());
                item.recalcTotalPrice();
                item.setOrder(order);
                item.setCreatedAt(now);
                item.setUpdatedAt(now);

                totalAmount += item.getTotalPrice();

            } catch (Exception e) {
                throw new RuntimeException("❌ Lỗi khi gọi product-service: " + e.getMessage());
            }
        }

        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        return orderRepository.save(order);
    }

    /** 🟢 Tạo đơn hàng từ DTO */
    @Transactional
public Order createOrderFromDTO(OrderCreateDTO dto) {
    if (dto.getItems() == null || dto.getItems().isEmpty()) {
        throw new RuntimeException("⚠️ Đơn hàng phải có ít nhất một sản phẩm!");
    }

    Order order = new Order();
    order.setCustomerName(dto.getCustomerName());
    order.setCustomerEmail(dto.getCustomerEmail());

    // Tạo token service-to-service
    String token = jwtUtil.generateToken("order-service");
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);

    LocalDateTime now = LocalDateTime.now();
    List<OrderItem> items = new ArrayList<>();
    double totalAmount = 0;

    for (OrderItemDTO itemDTO : dto.getItems()) {
        try {
            // 1️⃣ Lấy thông tin sản phẩm
            String productUrl = "http://localhost:8081/products/api/" + itemDTO.getProductId();
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<ProductResponse> response = restTemplate.exchange(
                    productUrl, HttpMethod.GET, entity, ProductResponse.class
            );

            ProductResponse product = response.getBody();
            if (product == null) {
                throw new RuntimeException("❌ Không tìm thấy sản phẩm ID = " + itemDTO.getProductId());
            }

            // 2️⃣ Kiểm tra tồn kho
            if (product.getQuantity() < itemDTO.getQuantity()) {
                throw new RuntimeException("❌ Sản phẩm '" + product.getName() + "' không đủ tồn kho!");
            }

            // 3️⃣ Giảm tồn kho bên product-service
            // 3️⃣ Giảm tồn kho bên product-service
String decreaseUrl = "http://localhost:8081/products/api/" 
                     + itemDTO.getProductId() 
                     + "/decrease?amount=" + itemDTO.getQuantity();
restTemplate.postForEntity(decreaseUrl, new HttpEntity<>(headers), Void.class);


            // 4️⃣ Tạo OrderItem và tính toán tổng tiền
            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(product.getPrice());
            item.recalcTotalPrice();
            item.setOrder(order);
            item.setCreatedAt(now);
            item.setUpdatedAt(now);

            totalAmount += item.getTotalPrice();
            items.add(item);

        } catch (Exception e) {
            throw new RuntimeException("❌ Lỗi khi gọi product-service: " + e.getMessage());
        }
    }

    order.setItems(items);
    order.setTotalAmount(totalAmount);
    order.setStatus("PENDING");
    order.setCreatedAt(now);
    order.setUpdatedAt(now);

    return orderRepository.save(order);
}



    /** 🟢 Cập nhật đơn hàng */
    @Transactional
    public Order updateOrder(Long id, Order orderDetails) {
        String token = jwtUtil.generateToken("order-service");
String authHeader = "Bearer " + token;


        return orderRepository.findById(id).map(order -> {
            order.setCustomerName(orderDetails.getCustomerName());
            order.setCustomerEmail(orderDetails.getCustomerEmail());
            order.setStatus(orderDetails.getStatus() != null ? orderDetails.getStatus() : "PENDING");

            LocalDateTime now = LocalDateTime.now();

            if (orderDetails.getItems() != null && !orderDetails.getItems().isEmpty()) {
                order.getItems().clear();
                double totalAmount = 0;
                for (OrderItem item : orderDetails.getItems()) {
                    try {
                        String productUrl = "http://localhost:8081/products/" + item.getProductId();
                        HttpHeaders headers = new HttpHeaders();
                        headers.set("Authorization", authHeader);
                        HttpEntity<Void> entity = new HttpEntity<>(headers);

                        ResponseEntity<ProductResponse> response = restTemplate.exchange(
                                productUrl, HttpMethod.GET, entity, ProductResponse.class
                        );

                        ProductResponse product = response.getBody();
                        if (product == null) {
                            throw new RuntimeException("❌ Không tìm thấy sản phẩm ID = " + item.getProductId());
                        }

                        item.setProductName(product.getName());
                        item.setUnitPrice(product.getPrice());
                        item.recalcTotalPrice();
                        item.setOrder(order);
                        item.setCreatedAt(now);
                        item.setUpdatedAt(now);

                        totalAmount += item.getTotalPrice();
                        order.getItems().add(item);

                    } catch (Exception e) {
                        throw new RuntimeException("❌ Lỗi khi gọi product-service: " + e.getMessage());
                    }
                }

                order.setTotalAmount(totalAmount);
            }

            order.setUpdatedAt(now);
            return orderRepository.save(order);
        }).orElseThrow(() -> new RuntimeException("❌ Không tìm thấy Order có ID = " + id));
    }

    /** Xóa đơn hàng */
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("❌ Không tìm thấy Order có ID = " + id);
        }
        orderRepository.deleteById(id);
    }

    /** Lớp ánh xạ phản hồi từ product-service */
    static class ProductResponse {
        private Long id;
        private String name;
        private Double price;
        private Integer quantity;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}

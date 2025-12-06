package org.example.orderservice.controller;

import org.example.orderservice.client.ProductClient;
import org.example.orderservice.dto.OrderDTO.OrderCreateDTO;
import org.example.orderservice.dto.OrderDTO.OrderItemDTO;
import org.example.orderservice.model.Order;
import org.example.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/orders-page")
public class OrderPageController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductClient productClient;

    @GetMapping
    public String ordersPage(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("products", productClient.getAllProducts());

        // Khởi tạo OrderCreateDTO + items tương ứng sản phẩm
        OrderCreateDTO orderForm = new OrderCreateDTO();
        List<OrderItemDTO> items = new ArrayList<>();
        productClient.getAllProducts().forEach(p -> {
            OrderItemDTO item = new OrderItemDTO();
            item.setProductId(p.getId());
            item.setQuantity(0);  // mặc định 0
            items.add(item);
        });
        orderForm.setItems(items);

        model.addAttribute("orderForm", orderForm);
        return "orders"; // orders.html
    }

    @PostMapping("/create")
    public String createOrder(@ModelAttribute("orderForm") OrderCreateDTO dto, RedirectAttributes redirectAttributes) {
        try {
            orderService.createOrderFromDTO(dto);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Tạo đơn hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/orders-page"; // redirect để load lại dữ liệu mới
    }

    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy Order với ID = " + id));
        model.addAttribute("order", order);
        return "order-detail";
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        orderService.deleteOrder(id);
        redirectAttributes.addFlashAttribute("successMessage", "✅ Xóa đơn hàng thành công!");
        return "redirect:/orders-page";
    }
}

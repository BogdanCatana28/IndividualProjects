package bnv.registru.registru.controllers;

import bnv.registru.registru.dtos.OrderDTO;
import bnv.registru.registru.services.OrderDocumentService;
import bnv.registru.registru.services.OrderService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;
    private final OrderDocumentService orderDocumentService;

    public OrderController(OrderService orderService, OrderDocumentService orderDocumentService) {
        this.orderService = orderService;
        this.orderDocumentService = orderDocumentService;
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        OrderDTO createdOrder = orderService.createOrder(orderDTO);
        return ResponseEntity.status(201).body(createdOrder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        OrderDTO orderDTO = orderService.getOrderById(id);
        return ResponseEntity.ok(orderDTO);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<OrderDTO> orders = orderService.getAllOrders(page, size);
        long totalOrders = orderService.countOrders();
        Map<String, Object> response = Map.of(
                "orders", orders,
                "totalOrders", totalOrders
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrder(
            @PathVariable Long id, @RequestBody OrderDTO orderDTO) {
        OrderDTO updatedOrder = orderService.updateOrder(id, orderDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{orderId}/download")
    public ResponseEntity<byte[]> downloadOrderDocument(@PathVariable Long orderId) throws IOException {
        OrderDTO orderDTO = orderService.getOrderById(orderId);

        String registrationDate = orderDTO.getRegistrationDate().toString();
        String recipient = orderDTO.getRecipient();

        ByteArrayOutputStream document = orderDocumentService.generateOrderDocument(orderId, registrationDate, recipient);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=comanda_" + orderId + ".docx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(document.toByteArray());
    }
}

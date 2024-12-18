package bnv.registru.registru.services.impl;

import bnv.registru.registru.builders.OrderBuilder;
import bnv.registru.registru.dtos.OrderDTO;
import bnv.registru.registru.entities.Order;
import bnv.registru.registru.repositories.OrderRepository;
import bnv.registru.registru.services.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderBuilder orderBuilder;

    public OrderServiceImpl(OrderRepository orderRepository, OrderBuilder orderBuilder) {
        this.orderRepository = orderRepository;
        this.orderBuilder = orderBuilder;
    }

    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) {

        if (orderDTO.getIssuer() == null || orderDTO.getIssuer().isEmpty()) {
            throw new RuntimeException("Issuer (Emitent) is required");
        }

        if (orderDTO.getRecipient() == null || orderDTO.getRecipient().isEmpty()) {
            throw new RuntimeException("Recipient (Destinatar) is required");
        }

        if (!"Bonelvio".equalsIgnoreCase(orderDTO.getIssuer())) {
            if (orderDTO.getDocumentId() == null || orderDTO.getDocumentId().isEmpty()) {
                throw new RuntimeException("Document ID (Numar de inregistrare la care se conexeaza si indicativul dosarului) is required");
            }
            if (orderDTO.getDocumentDate() == null) {
                throw new RuntimeException("Document Date (Data Documentului) is required");
            }
        }

        if (orderDTO.getRegistrationDate() == null) {
            orderDTO.setRegistrationDate(ZonedDateTime.now(ZoneId.of("Europe/Bucharest")).toLocalDate());
        }

        if (orderDTO.getDocumentDate() == null && !"Bonelvio".equalsIgnoreCase(orderDTO.getIssuer())) {
            orderDTO.setDocumentDate(ZonedDateTime.now(ZoneId.of("Europe/Bucharest")).toLocalDate());
        } else if (orderDTO.getDocumentDate() != null) {
            orderDTO.setDocumentDate(orderDTO.getDocumentDate().atStartOfDay(ZoneId.of("Europe/Bucharest")).toLocalDate());
        }

        if ("Bonelvio".equalsIgnoreCase(orderDTO.getIssuer())) {
            orderDTO.setDocumentId(String.valueOf(orderDTO.getOrderId()));
            orderDTO.setDocumentDate(orderDTO.getRegistrationDate());
        }

        Order order = orderBuilder.toEntity(orderDTO);
        Order savedOrder = orderRepository.save(order);

        if ("Bonelvio".equalsIgnoreCase(orderDTO.getIssuer())) {
            savedOrder.setDocumentId(String.valueOf(savedOrder.getOrderId()));
            savedOrder = orderRepository.save(savedOrder);
        }

        return orderBuilder.toDTO(savedOrder);
    }

    @Override
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (orderDTO.getIssuer() == null || orderDTO.getIssuer().isEmpty()) {
            throw new RuntimeException("Emitent cannot be empty");
        }
        if (orderDTO.getRecipient() == null || orderDTO.getRecipient().isEmpty()) {
            throw new RuntimeException("Destinatar cannot be empty");
        }

        if ("Bonelvio".equalsIgnoreCase(orderDTO.getIssuer())) {
            orderDTO.setDocumentId(existingOrder.getDocumentId());
            orderDTO.setDocumentDate(existingOrder.getDocumentDate());
        }

        existingOrder.setIssuer(orderDTO.getIssuer());
        existingOrder.setRecipient(orderDTO.getRecipient());
        existingOrder.setExtraNotes(orderDTO.getExtraNotes());
        existingOrder.setDocumentId(orderDTO.getDocumentId());
        existingOrder.setDocumentDate(orderDTO.getDocumentDate());

        Order updatedOrder = orderRepository.save(existingOrder);
        return orderBuilder.toDTO(updatedOrder);
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return orderBuilder.toDTO(order);
    }

    @Override
    public List<OrderDTO> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderId").ascending()); // Sort by `orderId`
        Page<Order> ordersPage = orderRepository.findAll(pageable);
        return ordersPage.stream()
                .map(orderBuilder::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long countOrders() {
        return orderRepository.count();
    }

    @Override
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found");
        }
        orderRepository.deleteById(id);
    }
}

package bnv.registru.registru.services;

import bnv.registru.registru.dtos.OrderDTO;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(OrderDTO orderDTO);
    OrderDTO getOrderById(Long id);
    OrderDTO updateOrder(Long id, OrderDTO orderDTO);

    List<OrderDTO> getAllOrders(int page, int size);

    long countOrders();

    void deleteOrder(Long id);
}

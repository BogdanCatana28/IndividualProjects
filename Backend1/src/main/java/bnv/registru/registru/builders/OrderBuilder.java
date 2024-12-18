package bnv.registru.registru.builders;

import bnv.registru.registru.dtos.OrderDTO;
import bnv.registru.registru.entities.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class OrderBuilder {

    public OrderDTO toDTO(Order order) {
        return OrderDTO.builder()
                .orderId(order.getOrderId())
                .registrationDate(order.getRegistrationDate())
                .documentId(order.getDocumentId())
                .documentDate(order.getDocumentDate())
                .issuer(order.getIssuer())
                .documentType(order.getDocumentType())
                .department(order.getDepartment())
                .recipient(order.getRecipient())
                .extraNotes(order.getExtraNotes())
                .build();
    }

    public Order toEntity(OrderDTO orderDTO) {
        return Order.builder()
                .orderId(orderDTO.getOrderId())
                .registrationDate(orderDTO.getRegistrationDate() != null
                        ? orderDTO.getRegistrationDate()
                        : LocalDate.now())
                .documentId(orderDTO.getDocumentId())
                .documentDate(orderDTO.getDocumentDate() != null
                        ? orderDTO.getDocumentDate()
                        : LocalDate.now())
                .issuer(orderDTO.getIssuer())
                .documentType("Comanda")
                .department("Transport")
                .recipient(orderDTO.getRecipient())
                .extraNotes(orderDTO.getExtraNotes())
                .build();
    }
}

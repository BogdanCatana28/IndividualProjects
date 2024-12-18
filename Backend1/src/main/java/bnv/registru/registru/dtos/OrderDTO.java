package bnv.registru.registru.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    private Long orderId;
    private LocalDate registrationDate;
    private String documentId;
    private LocalDate documentDate;
    private String issuer;
    private String documentType;
    private String department;
    private String recipient;
    private String extraNotes;
}

package bnv.registru.registru.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false)
    private LocalDate registrationDate;

    @Column(nullable = false, unique = true)
    private String documentId;

    @Column(nullable = false)
    private LocalDate documentDate;

    @Column(nullable = false)
    private String issuer;

    @Column(nullable = false)
    private String documentType;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String recipient;

    @Column
    private String extraNotes;

    @PrePersist
    private void setDefaultValues() {
        if (this.registrationDate == null) {
            this.registrationDate = LocalDate.now();
        }
        if (this.documentDate == null) {
            this.documentDate = LocalDate.now();
        }
        if (this.documentType == null) {
            this.documentType = "Comanda";
        }
        if (this.department == null) {
            this.department = "Transport";
        }
    }
}

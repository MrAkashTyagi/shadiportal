package com.bigsquare.ShadiPortal.entities;

import com.bigsquare.ShadiPortal.enums.GiftType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "gift_distribution",
        indexes = {
                @Index(
                        name = "idx_gift_distribution_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_gift_distribution_type",
                        columnList = "gift_type"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiftDistribution {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "gift_distribution_id"
    )
    private Integer id;

    @Column(
            name = "recipient_name",
            nullable = false,
            length = 150
    )
    private String recipientName;

    @Column(
            name = "mobile_number",
            length = 20
    )
    private String mobileNumber;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "gift_type",
            nullable = false,
            length = 30
    )
    private GiftType giftType;

    @Column(
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal amount;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(length = 1000)
    private String notes;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    @JsonIgnoreProperties({
            "password",
            "guests",
            "families",
            "expenses"
    })
    private User user;

    @PrePersist
    public void prePersist() {

        LocalDateTime now =
                LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (quantity == null) {
            quantity = 1;
        }

        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void preUpdate() {

        updatedAt =
                LocalDateTime.now();
    }

    @Transient
    public BigDecimal getTotalAmount() {

        BigDecimal safeAmount =
                amount != null
                        ? amount
                        : BigDecimal.ZERO;

        int safeQuantity =
                quantity != null
                        ? quantity
                        : 1;

        return safeAmount.multiply(
                BigDecimal.valueOf(
                        safeQuantity
                )
        );
    }
}

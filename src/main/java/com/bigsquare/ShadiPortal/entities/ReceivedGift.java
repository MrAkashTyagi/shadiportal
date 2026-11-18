package com.bigsquare.ShadiPortal.entities;

import com.bigsquare.ShadiPortal.enums.ReceivedGiftType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "received_gift",
        indexes = {
                @Index(
                        name = "idx_received_gift_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_received_gift_type",
                        columnList = "gift_type"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceivedGift {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "received_gift_id"
    )
    private Integer id;

    @Column(
            name = "giver_name",
            nullable = false,
            length = 150
    )
    private String giverName;

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
    private ReceivedGiftType giftType;

    @Column(
            name = "cash_amount",
            precision = 15,
            scale = 2
    )
    private BigDecimal cashAmount;

    @Column(
            name = "gift_description",
            length = 500
    )
    private String giftDescription;

    @Column(
            nullable = false
    )
    private Integer quantity = 1;

    @Column(
            length = 1000
    )
    private String notes;

    @Column(
            name = "received_at"
    )
    private LocalDateTime receivedAt;

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
    public void applyDefaults() {

        LocalDateTime now =
                LocalDateTime.now();

        if (quantity == null) {
            quantity = 1;
        }

        if (receivedAt == null) {
            receivedAt = now;
        }

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void updateTimestamp() {

        updatedAt =
                LocalDateTime.now();
    }
}

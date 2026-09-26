package com.bigsquare.ShadiPortal.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "expense_bills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "expense")
public class ExpenseBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 1000)
    private String billUrl;

    @Column(nullable = false, length = 500)
    private String billPublicId;

    @Column(nullable = false, length = 50)
    private String billResourceType;

    @Column(nullable = false, length = 255)
    private String billOriginalName;

    @Column(nullable = false, length = 100)
    private String billContentType;

    private Long billFileSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "expense_id",
            nullable = false
    )
    @JsonIgnore
    private Expense expense;
}

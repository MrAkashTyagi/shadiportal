package com.bigsquare.ShadiPortal.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Expense {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String expenseName;
    private String category;
    private String description;
//    private BigDecimal amount;
    private String billPath;
    private LocalDate expenseDate;
    private String paidBy;

    private Double totalAmount;

    private Double paidAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({
            "password",
            "guests",
            "families",
            "expenses"
    })
    private User user;


}

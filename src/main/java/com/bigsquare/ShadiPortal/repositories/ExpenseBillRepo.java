package com.bigsquare.ShadiPortal.repositories;

import com.bigsquare.ShadiPortal.entities.ExpenseBill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpenseBillRepo
        extends JpaRepository<
        ExpenseBill,
        Integer
        > {

    Optional<ExpenseBill>
    findByIdAndExpenseId(
            Integer billId,
            Integer expenseId
    );

}

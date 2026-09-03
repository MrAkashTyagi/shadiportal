package com.bigsquare.ShadiPortal.repositories;

import com.bigsquare.ShadiPortal.entities.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseRepo extends JpaRepository<Expense, Integer> {

    @Query("""
            SELECT e
            FROM Expense e
            WHERE
                (
                    :search IS NULL OR
                    :search = '' OR
                    LOWER(e.expenseName)
                    LIKE LOWER(CONCAT('%', :search, '%'))
                )
            AND
                (
                    :category IS NULL OR
                    :category = '' OR
                    LOWER(e.category) = LOWER(:category)
                )
            """)
    Page<Expense> findBySearchAndCategory(
            @Param("search") String search,
            @Param("category") String category,
            Pageable pageable
    );

    @Query("""
       SELECT e.category,
              SUM(e.amount)
       FROM Expense e
       GROUP BY e.category
       """)
    List<Object[]> getCategoryWiseExpense();
}

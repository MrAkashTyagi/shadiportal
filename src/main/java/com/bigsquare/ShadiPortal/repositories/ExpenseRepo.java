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
            e.user.id = :userId
            AND
            (
                :search IS NULL
                OR :search = ''
                OR LOWER(e.expenseName)
                LIKE LOWER(CONCAT('%', :search, '%'))
            )
            AND
            (
                :category IS NULL
                OR :category = ''
                OR LOWER(e.category) = LOWER(:category)
            )
        """)
    Page<Expense> findBySearchAndCategory(

            @Param("userId")
            Integer userId,

            @Param("search")
            String search,

            @Param("category")
            String category,

            Pageable pageable
    );

//    @Query("""
//            SELECT e.category,
//                   SUM(e.amount)
//            FROM Expense e
//            GROUP BY e.category
//            """)
//    List<Object[]> getCategoryWiseExpense();


    @Query("""
       SELECT e.category,
              SUM(e.totalAmount)
       FROM Expense e
       WHERE e.category IS NOT NULL
       GROUP BY e.category
       """)
    List<Object[]> getCategoryWiseExpense();

//    @Query("""
//            SELECT COALESCE(SUM(e.amount),0)
//            FROM Expense e
//            """)
//    Double getTotalExpenseAmount();
//
//    Page<Expense> findAllByOrderByIdDesc(
//            Pageable pageable
//    );

    @Query("""
        SELECT COALESCE(SUM(e.totalAmount),0)
        FROM Expense e
        """)
    Double getTotalExpenseAmount();

    @Query("""
        SELECT COALESCE(SUM(e.paidAmount),0)
        FROM Expense e
        """)
    Double getTotalPaidExpenseAmount();

    Page<Expense> findAllByOrderByIdDesc(
            Pageable pageable
    );


    Page<Expense> findAllByUserId(
            Integer userId,
            Pageable pageable
    );

    List<Expense> findAllByUserId(
            Integer userId
    );

    @Query("""
       SELECT COALESCE(
           SUM(e.totalAmount),
           0
       )
       FROM Expense e
       WHERE e.user.id = :userId
       """)
    Double getTotalExpenseAmountByUserId(
            @Param("userId") Integer userId
    );

    @Query("""
       SELECT COALESCE(
           SUM(e.paidAmount),
           0
       )
       FROM Expense e
       WHERE e.user.id = :userId
       """)
    Double getTotalPaidExpenseAmountByUserId(
            @Param("userId") Integer userId
    );

}

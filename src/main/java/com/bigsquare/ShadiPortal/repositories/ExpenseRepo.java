package com.bigsquare.ShadiPortal.repositories;

import com.bigsquare.ShadiPortal.entities.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

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


    @Query("""
   SELECT e.category,
          SUM(e.totalAmount)
   FROM Expense e
   WHERE e.category IS NOT NULL
     AND e.user.id = :userId
   GROUP BY e.category
   """)
    List<Object[]> getCategoryWiseExpense(
            Integer userId
    );

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

    Page<Expense> findByUserIdOrderByIdDesc(
            Long userId,
            Pageable pageable);


    @Query("""
    SELECT e
    FROM Expense e
    WHERE e.user.id = :userId

      AND (
          :search = ''
          OR LOWER(e.expenseName)
             LIKE LOWER(
                 CONCAT('%', :search, '%')
             )
          OR LOWER(e.paidBy)
             LIKE LOWER(
                 CONCAT('%', :search, '%')
             )
      )

      AND (
          :category = ''
          OR e.category = :category
      )

    ORDER BY e.id DESC
    """)
    List<Expense> findForExport(
            @Param("userId") Integer userId,
            @Param("search") String search,
            @Param("category") String category
    );

    Optional<Expense> findByIdAndUserId(
            Integer id,
            Integer userId
    );

    @Query("""
    SELECT e
    FROM Expense e
    WHERE e.user.id = :userId

      AND (
          :search IS NULL
          OR :search = ''
          OR LOWER(e.expenseName)
             LIKE LOWER(
                 CONCAT('%', :search, '%')
             )
          OR LOWER(
                 COALESCE(e.description, '')
             )
             LIKE LOWER(
                 CONCAT('%', :search, '%')
             )
      )

      AND (
          :category IS NULL
          OR :category = ''
          OR LOWER(e.category)
             = LOWER(:category)
      )

      AND (
          :paidBy IS NULL
          OR :paidBy = ''
          OR LOWER(
                 COALESCE(e.paidBy, '')
             )
             LIKE LOWER(
                 CONCAT('%', :paidBy, '%')
             )
      )

      AND (
          :fromDate IS NULL
          OR e.expenseDate >= :fromDate
      )

      AND (
          :toDate IS NULL
          OR e.expenseDate <= :toDate
      )

      AND (
          :paymentStatus IS NULL
          OR :paymentStatus = ''

          OR (
              :paymentStatus = 'PAID'
              AND e.totalAmount IS NOT NULL
              AND COALESCE(e.paidAmount, 0.0)
                  >= e.totalAmount
          )

          OR (
              :paymentStatus = 'PARTIAL'
              AND e.totalAmount IS NOT NULL
              AND COALESCE(e.paidAmount, 0.0) > 0.0
              AND COALESCE(e.paidAmount, 0.0)
                  < e.totalAmount
          )

          OR (
              :paymentStatus = 'PENDING'
              AND COALESCE(e.paidAmount, 0.0) <= 0.0
          )
      )
    """)
    Page<Expense> findByFilters(

            @Param("userId")
            Integer userId,

            @Param("search")
            String search,

            @Param("category")
            String category,

            @Param("paymentStatus")
            String paymentStatus,

            @Param("paidBy")
            String paidBy,

            @Param("fromDate")
            LocalDate fromDate,

            @Param("toDate")
            LocalDate toDate,

            Pageable pageable
    );

}

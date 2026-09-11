package com.bigsquare.ShadiPortal.services;

import com.bigsquare.ShadiPortal.dto.ExpenseCategorySummaryDto;
import com.bigsquare.ShadiPortal.dto.ExpenseSummaryDto;
import com.bigsquare.ShadiPortal.entities.Expense;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExpenseService {

    // Create
//    Expense createExpense(Expense expense, MultipartFile bill);

    Expense createExpense(
            Expense expense,
            MultipartFile bill,
            Integer userId
    );

    // Update
    Expense updateExpense(Integer id, Expense expense, MultipartFile bill);

    // Get By Id
    Expense getExpenseById(Integer id);

    // Get All
    List<Expense> getAllExpenses();

    // Paginated + Search + Category Filter
    Page<Expense> getPaginatedExpenses(
            Integer userId,
            int page,
            int size,
            String search,
            String category
    );

    // Delete
    void deleteExpense(Integer id);

    // Export
    byte[] exportExpenses();

    ExpenseSummaryDto getExpenseSummary(Integer userId);

    List<ExpenseCategorySummaryDto>
    getExpenseCategorySummary();


}

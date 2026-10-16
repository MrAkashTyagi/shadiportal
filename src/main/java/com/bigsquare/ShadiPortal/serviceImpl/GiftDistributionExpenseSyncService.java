package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.entities.Expense;
import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.repositories.ExpenseRepo;
import com.bigsquare.ShadiPortal.repositories.GiftDistributionRepository;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GiftDistributionExpenseSyncService {

    public static final String SOURCE_TYPE =
            "GIFT_DISTRIBUTION";

    private final GiftDistributionRepository
            giftDistributionRepository;

    private final ExpenseRepo expenseRepo;

    private final UserRepo userRepo;

    @Transactional
    public void syncGiftDistributionExpense(
            Integer userId
    ) {

        if (userId == null) {

            throw new IllegalArgumentException(
                    "User id is required"
            );
        }

        User user =
                userRepo
                        .findById(userId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "User not found with id: "
                                                + userId
                                )
                        );

        BigDecimal aggregateAmount =
                giftDistributionRepository
                        .getTotalAmountByUserId(
                                userId
                        );

        double totalAmount =
                aggregateAmount != null
                        ? aggregateAmount.doubleValue()
                        : 0.0;

        Expense expense =
                expenseRepo
                        .findByUserIdAndSourceType(
                                userId,
                                SOURCE_TYPE
                        )
                        .orElseGet(() ->
                                createSystemExpense(
                                        user
                                )
                        );

        expense.setExpenseName(
                "Gifts To Give"
        );

        expense.setCategory(
                "Gift Distribution"
        );

        expense.setDescription(
                "System generated expense from gifts to give"
        );

        expense.setTotalAmount(
                totalAmount
        );

        expense.setPaidAmount(
                0.0
        );

        expense.setExpenseDate(
                LocalDate.now()
        );

        expense.setPaidBy(
                null
        );

        expense.setSystemGenerated(
                true
        );

        expense.setSourceType(
                SOURCE_TYPE
        );

        expense.setUser(user);

        expenseRepo.save(expense);
    }

    private Expense createSystemExpense(
            User user
    ) {

        Expense expense =
                new Expense();

        expense.setUser(user);

        expense.setSystemGenerated(
                true
        );

        expense.setSourceType(
                SOURCE_TYPE
        );

        expense.setTotalAmount(
                0.0
        );

        expense.setPaidAmount(
                0.0
        );

        return expense;
    }
}

package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.entities.Expense;
import com.bigsquare.ShadiPortal.entities.Guest;
import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.repositories.ExpenseRepo;
import com.bigsquare.ShadiPortal.repositories.GuestRepo;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class GuestEnvelopeExpenseSyncService {

    public static final String SOURCE_TYPE =
            "GUEST_ENVELOPE";

    private final GuestRepo guestRepo;

    private final ExpenseRepo expenseRepo;

    private final UserRepo userRepo;

    public GuestEnvelopeExpenseSyncService(
            GuestRepo guestRepo,
            ExpenseRepo expenseRepo,
            UserRepo userRepo
    ) {
        this.guestRepo = guestRepo;
        this.expenseRepo = expenseRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public void syncEnvelopeExpense(
            Integer userId
    ) {
        User user =
                userRepo.findById(userId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "User not found with id: "
                                                + userId
                                )
                        );

        List<Guest> guests =
                guestRepo.findAllByUserId(
                        userId
                );

        double envelopeTotal =
                guests.stream()
                        .map(Guest::getCash)
                        .map(this::parseCashAmount)
                        .filter(amount ->
                                amount > 0
                        )
                        .mapToDouble(
                                Double::doubleValue
                        )
                        .sum();

        Expense envelopeExpense =
                expenseRepo
                        .findByUserIdAndSourceType(
                                userId,
                                SOURCE_TYPE
                        )
                        .orElseGet(
                                Expense::new
                        );

        boolean newExpense =
                envelopeExpense.getId() == null;

        envelopeExpense.setExpenseName(
                "Guest Cash Envelopes"
        );

        envelopeExpense.setCategory(
                "Gift"
        );

        envelopeExpense.setDescription(
                "Automatically calculated from guest cash envelope planning."
        );

        envelopeExpense.setTotalAmount(
                envelopeTotal
        );

        envelopeExpense.setPaidAmount(
                0.0
        );

        envelopeExpense.setPaidBy(
                "System"
        );

        envelopeExpense.setSystemGenerated(
                true
        );

        envelopeExpense.setSourceType(
                SOURCE_TYPE
        );

        envelopeExpense.setUser(
                user
        );

        if (
                newExpense
                        || envelopeExpense.getExpenseDate() == null
        ) {
            envelopeExpense.setExpenseDate(
                    LocalDate.now()
            );
        }

        expenseRepo.save(
                envelopeExpense
        );
    }

    private Double parseCashAmount(
            String cash
    ) {
        if (
                cash == null
                        || cash.isBlank()
        ) {
            return 0.0;
        }

        String normalizedCash =
                cash.trim()
                        .replace("₹", "")
                        .replace(",", "")
                        .replaceAll(
                                "[^0-9]",
                                ""
                        );

        if (normalizedCash.isBlank()) {
            return 0.0;
        }

        try {
            return Double.valueOf(
                    normalizedCash
            );

        } catch (
                NumberFormatException exception
        ) {
            return 0.0;
        }
    }
}

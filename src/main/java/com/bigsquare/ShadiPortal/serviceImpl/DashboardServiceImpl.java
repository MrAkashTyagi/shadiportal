package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.dto.DashboardSummaryDto;
import com.bigsquare.ShadiPortal.entities.Expense;
import com.bigsquare.ShadiPortal.entities.Guest;
import com.bigsquare.ShadiPortal.repositories.ExpenseRepo;
import com.bigsquare.ShadiPortal.repositories.FamilyRepo;
import com.bigsquare.ShadiPortal.repositories.GuestRepo;
import com.bigsquare.ShadiPortal.security.CurrentUserService;
import com.bigsquare.ShadiPortal.services.DashboardService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl
        implements DashboardService {

    private final GuestRepo guestRepo;

    private final FamilyRepo familyRepo;

    private final ExpenseRepo expenseRepo;

    private final CurrentUserService currentUserService;

    public DashboardServiceImpl(
            GuestRepo guestRepo,
            FamilyRepo familyRepo,
            ExpenseRepo expenseRepo,
            CurrentUserService currentUserService
    ) {

        this.guestRepo = guestRepo;
        this.familyRepo = familyRepo;
        this.expenseRepo = expenseRepo;
        this.currentUserService = currentUserService;
    }

    @Override
    public DashboardSummaryDto getDashboardSummary() {

        Integer userId =
                currentUserService.getCurrentUserId();

        Long totalGuests =
                guestRepo.countByUserId(
                        userId
                );

        Long totalFamilies =
                familyRepo.countByUserId(
                        userId
                );

        Long totalFamilyMembers =
                familyRepo
                        .getTotalFamilyMembersByUserId(
                                userId
                        );

        double averageFamilySize =
                totalFamilies > 0
                        ? (double) totalFamilyMembers
                          / totalFamilies
                        : 0.0;

        Long invitationSent =
                guestRepo
                        .countByUserIdAndInvitationSentTrue(
                                userId
                        );

        Long pendingInvitations =
                totalGuests - invitationSent;

        Long stayRequired =
                guestRepo.countByUserIdAndStay(
                        userId,
                        "Yes"
                );

        Double totalExpense =
                expenseRepo
                        .getTotalExpenseAmountByUserId(
                                userId
                        );

        Double totalPaidExpense =
                expenseRepo
                        .getTotalPaidExpenseAmountByUserId(
                                userId
                        );

        /*
         * Null-safe handling in case aggregate queries
         * return null when the user has no expenses.
         */
        totalExpense =
                totalExpense != null
                        ? totalExpense
                        : 0.0;

        totalPaidExpense =
                totalPaidExpense != null
                        ? totalPaidExpense
                        : 0.0;

        Double totalPendingExpense =
                totalExpense - totalPaidExpense;

        return new DashboardSummaryDto(
                totalGuests,
                totalFamilies,
                totalFamilyMembers,
                averageFamilySize,
                totalExpense,
                totalPaidExpense,
                totalPendingExpense,
                stayRequired,
                invitationSent,
                pendingInvitations
        );
    }

    @Override
    public List<Guest> getRecentGuests() {

        Integer userId =
                currentUserService.getCurrentUserId();

        return guestRepo
                .findByUserIdOrderByIdDesc(
                        userId.longValue(),
                        PageRequest.of(
                                0,
                                5
                        )
                )
                .getContent();
    }

    @Override
    public List<Expense> getRecentExpenses() {

        Integer userId =
                currentUserService.getCurrentUserId();

        return expenseRepo
                .findByUserIdOrderByIdDesc(
                        userId.longValue(),
                        PageRequest.of(
                                0,
                                5
                        )
                )
                .getContent();
    }
}

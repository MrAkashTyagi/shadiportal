package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.dto.DashboardSummaryDto;
import com.bigsquare.ShadiPortal.entities.Expense;
import com.bigsquare.ShadiPortal.entities.Guest;
import com.bigsquare.ShadiPortal.repositories.ExpenseRepo;
import com.bigsquare.ShadiPortal.repositories.FamilyRepo;
import com.bigsquare.ShadiPortal.repositories.GuestRepo;
import com.bigsquare.ShadiPortal.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {


    @Autowired
    private GuestRepo guestRepo;

    @Autowired
    private FamilyRepo familyRepo;

    @Autowired
    private ExpenseRepo expenseRepo;

    @Override
    public DashboardSummaryDto getDashboardSummary(
            Integer userId
    ) {

        Long totalGuests =
                guestRepo.countByUserId(userId);

        Long totalFamilies =
                familyRepo.countByUserId(userId);

        Long totalFamilyMembers =
                familyRepo.getTotalFamilyMembersByUserId(
                        userId
                );

        Double averageFamilySize =
                totalFamilies > 0
                        ? (double) totalFamilyMembers / totalFamilies
                        : 0.0;

//        Long invitationSent =
//                guestRepo.countByInvitationSentTrue();

        Long invitationSent =
                guestRepo.countByUserIdAndInvitationSentTrue(
                        userId
                );


//        Long pendingInvitations =
//                guestRepo.countPendingInvitationsByUserId(userId);

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

        return guestRepo
                .findAllByOrderByIdDesc(
                        PageRequest.of(0, 5)
                )
                .getContent();

    }

    @Override
    public List<Expense> getRecentExpenses() {

        return expenseRepo
                .findAllByOrderByIdDesc(
                        PageRequest.of(0, 5)
                )
                .getContent();

    }

}

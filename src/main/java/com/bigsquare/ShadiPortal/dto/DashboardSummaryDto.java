package com.bigsquare.ShadiPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter

public class DashboardSummaryDto {

    private Long totalGuests;
    private Long totalFamilies;
    private Long totalFamilyMembers;

    private Double averageFamilySize;

    private Double totalExpense;

    private Long stayRequired;

    private Long invitationSent;
    private Long pendingInvitations;

    private Double totalPaidExpense;

    private Double totalPendingExpense;

    public DashboardSummaryDto(

            Long totalGuests,
            Long totalFamilies,
            Long totalFamilyMembers,

            Double averageFamilySize,

            Double totalExpense,
            Double totalPaidExpense,
            Double totalPendingExpense,

            Long stayRequired,
            Long invitationSent,
            Long pendingInvitations

    ) {
        this.totalGuests = totalGuests;
        this.totalFamilies = totalFamilies;
        this.totalFamilyMembers = totalFamilyMembers;

        this.averageFamilySize = averageFamilySize;

        this.totalExpense = totalExpense;
        this.totalPaidExpense = totalPaidExpense;
        this.totalPendingExpense = totalPendingExpense;

        this.stayRequired = stayRequired;
        this.invitationSent = invitationSent;
        this.pendingInvitations = pendingInvitations;
    }

    public DashboardSummaryDto() {
    }

    // getters setters
}

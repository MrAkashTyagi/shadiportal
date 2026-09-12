package com.bigsquare.ShadiPortal.services;

import com.bigsquare.ShadiPortal.dto.DashboardSummaryDto;
import com.bigsquare.ShadiPortal.entities.Expense;
import com.bigsquare.ShadiPortal.entities.Guest;

import java.util.List;

public interface DashboardService {

    DashboardSummaryDto getDashboardSummary(
            Integer userId
    );

//    List<Guest> getRecentGuests();

    List<Guest> getRecentGuests(Long userId);

//    List<Expense> getRecentExpenses();

    List<Expense> getRecentExpenses(Long userId);
}

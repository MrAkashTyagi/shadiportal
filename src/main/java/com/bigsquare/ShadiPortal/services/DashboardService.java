package com.bigsquare.ShadiPortal.services;

import com.bigsquare.ShadiPortal.dto.DashboardSummaryDto;
import com.bigsquare.ShadiPortal.entities.Expense;
import com.bigsquare.ShadiPortal.entities.Guest;

import java.util.List;

public interface DashboardService {

    DashboardSummaryDto getDashboardSummary();

    List<Guest> getRecentGuests();

    List<Expense> getRecentExpenses();
}

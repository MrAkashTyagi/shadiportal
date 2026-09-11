package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.dto.DashboardSummaryDto;
//import com.bigsquare.ShadiPortal.service.DashboardService;

//import com.bigsquare.ShadiPortal.services.DashboardService;
import com.bigsquare.ShadiPortal.entities.Expense;
import com.bigsquare.ShadiPortal.entities.Guest;
import com.bigsquare.ShadiPortal.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin("*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDto>
    getDashboardSummary(

            @RequestParam Integer userId

    ) {

        return ResponseEntity.ok(
                dashboardService.getDashboardSummary(
                        userId
                )
        );
    }

    @GetMapping("/recent-guests")
    public ResponseEntity<List<Guest>>
    getRecentGuests() {

        return ResponseEntity.ok(
                dashboardService.getRecentGuests()
        );

    }

    @GetMapping("/recent-expenses")
    public ResponseEntity<List<Expense>>
    getRecentExpenses() {

        return ResponseEntity.ok(
                dashboardService.getRecentExpenses()
        );

    }
}

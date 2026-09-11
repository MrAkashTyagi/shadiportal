package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.entities.Expense;
import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.helper.ExpenseHelper;
import com.bigsquare.ShadiPortal.repositories.ExpenseRepo;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExpenseDataDumpServiceImpl {

    @Autowired
    private ExpenseRepo expenseRepo;

    @Autowired
    private UserRepo userRepo;

    public void save(
            MultipartFile file,
            Integer userId
    ) {

        try {

            User user =
                    userRepo.findById(userId)
                            .orElseThrow(() ->
                                    new EntityNotFoundException(
                                            "User not found"
                                    )
                            );

            List<Expense> expenses =
                    ExpenseHelper.convertExcelToListOfExpenses(
                            file.getInputStream()
                    );

            for (Expense expense : expenses) {

                expense.setId(null);

                expense.setUser(user);
            }

            expenseRepo.saveAll(expenses);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error importing expenses",
                    e
            );
        }
    }

    public ByteArrayInputStream getActualData(
            Integer userId
    ) throws IOException {

        List<Expense> expenses =
                expenseRepo.findAllByUserId(userId);

        return ExpenseHelper.dataToExcel(
                expenses
        );
    }
}

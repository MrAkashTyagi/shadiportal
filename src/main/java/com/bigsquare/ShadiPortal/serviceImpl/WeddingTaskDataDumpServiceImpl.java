package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.entities.WeddingTask;
import com.bigsquare.ShadiPortal.helper.WeddingTaskHelper;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
import com.bigsquare.ShadiPortal.repositories.WeddingTaskRepo;
import com.bigsquare.ShadiPortal.security.CurrentUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeddingTaskDataDumpServiceImpl {

    private final WeddingTaskRepo weddingTaskRepo;

    private final UserRepo userRepo;

    private final CurrentUserService currentUserService;

    @Transactional
    public int save(
            MultipartFile file
    ) {

        if (file == null || file.isEmpty()) {

            throw new IllegalArgumentException(
                    "Excel file is required"
            );
        }

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        User user =
                userRepo.findById(userId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Current user not found"
                                )
                        );

        try {

            List<WeddingTask> tasks =
                    WeddingTaskHelper
                            .convertExcelToListOfTasks(
                                    file.getInputStream()
                            );

            if (tasks.isEmpty()) {

                throw new IllegalArgumentException(
                        "Excel file does not contain any task data"
                );
            }

            for (WeddingTask task : tasks) {

                task.setId(null);

                task.setUser(user);
            }

            return weddingTaskRepo
                    .saveAll(tasks)
                    .size();

        } catch (IllegalArgumentException exception) {

            throw exception;

        } catch (IOException exception) {

            throw new RuntimeException(
                    "Unable to read wedding tasks Excel file",
                    exception
            );

        } catch (Exception exception) {

            throw new RuntimeException(
                    "Error importing wedding tasks",
                    exception
            );
        }
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream getActualData()
            throws IOException {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        List<WeddingTask> tasks =
                weddingTaskRepo
                        .findAllByUserIdOrderByIdDesc(
                                userId
                        );

        return WeddingTaskHelper
                .dataToExcel(tasks);
    }
}

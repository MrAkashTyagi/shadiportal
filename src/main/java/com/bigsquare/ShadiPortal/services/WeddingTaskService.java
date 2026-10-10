package com.bigsquare.ShadiPortal.services;

import com.bigsquare.ShadiPortal.dto.WeddingTaskRequest;
import com.bigsquare.ShadiPortal.dto.WeddingTaskResponse;
import com.bigsquare.ShadiPortal.enums.TaskPriority;
import com.bigsquare.ShadiPortal.enums.TaskStatus;
import org.springframework.data.domain.Page;

public interface WeddingTaskService {

    WeddingTaskResponse createTask(
            WeddingTaskRequest request
    );

    WeddingTaskResponse updateTask(
            Long id,
            WeddingTaskRequest request
    );

    WeddingTaskResponse updateTaskStatus(
            Long id,
            TaskStatus status
    );

    WeddingTaskResponse getTaskById(
            Long id
    );

    Page<WeddingTaskResponse> getTasks(
            int page,
            int size,
            String search,
            TaskStatus status,
            TaskPriority priority
    );

    void deleteTask(
            Long id
    );
}

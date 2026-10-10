package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.dto.WeddingTaskRequest;
import com.bigsquare.ShadiPortal.dto.WeddingTaskResponse;
import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.entities.WeddingTask;
import com.bigsquare.ShadiPortal.enums.TaskPriority;
import com.bigsquare.ShadiPortal.enums.TaskStatus;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
import com.bigsquare.ShadiPortal.repositories.WeddingTaskRepo;
import com.bigsquare.ShadiPortal.security.CurrentUserService;
import com.bigsquare.ShadiPortal.services.WeddingTaskService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WeddingTaskServiceImpl
        implements WeddingTaskService {

    private final WeddingTaskRepo weddingTaskRepo;

    private final UserRepo userRepo;

    private final CurrentUserService currentUserService;

    public WeddingTaskServiceImpl(
            WeddingTaskRepo weddingTaskRepo,
            UserRepo userRepo,
            CurrentUserService currentUserService
    ) {
        this.weddingTaskRepo = weddingTaskRepo;
        this.userRepo = userRepo;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional
    public WeddingTaskResponse createTask(
            WeddingTaskRequest request
    ) {
        Integer userId = getCurrentUserId();
        User user = getUserById(userId);

        validateRequest(request);

        WeddingTask task = new WeddingTask();

        updateEntity(
                task,
                request
        );

        task.setUser(user);

        return mapToResponse(
                weddingTaskRepo.save(task)
        );
    }

    @Override
    @Transactional
    public WeddingTaskResponse updateTask(
            Long id,
            WeddingTaskRequest request
    ) {
        Integer userId = getCurrentUserId();

        validateRequest(request);

        WeddingTask existingTask =
                getTaskEntity(
                        id,
                        userId
                );

        updateEntity(
                existingTask,
                request
        );

        return mapToResponse(
                weddingTaskRepo.save(
                        existingTask
                )
        );
    }

    @Override
    @Transactional
    public WeddingTaskResponse updateTaskStatus(
            Long id,
            TaskStatus status
    ) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "Task status is required"
            );
        }

        Integer userId = getCurrentUserId();

        WeddingTask existingTask =
                getTaskEntity(
                        id,
                        userId
                );

        existingTask.setStatus(status);

        return mapToResponse(
                weddingTaskRepo.save(
                        existingTask
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public WeddingTaskResponse getTaskById(
            Long id
    ) {
        Integer userId = getCurrentUserId();

        return mapToResponse(
                getTaskEntity(
                        id,
                        userId
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WeddingTaskResponse> getTasks(
            int page,
            int size,
            String search,
            TaskStatus status,
            TaskPriority priority
    ) {
        Integer userId = getCurrentUserId();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Order.asc("status"),
                                Sort.Order.asc("dueDate"),
                                Sort.Order.desc("id")
                        )
                );

        return weddingTaskRepo
                .findTasksWithFilters(
                        userId,
                        normalize(search),
                        status,
                        priority,
                        pageable
                )
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public void deleteTask(
            Long id
    ) {
        Integer userId = getCurrentUserId();

        WeddingTask task =
                getTaskEntity(
                        id,
                        userId
                );

        weddingTaskRepo.delete(task);
    }

    private void validateRequest(
            WeddingTaskRequest request
    ) {
        if (
                request == null
                        || request.getTitle() == null
                        || request.getTitle().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Task title is required"
            );
        }
    }

    private void updateEntity(
            WeddingTask task,
            WeddingTaskRequest request
    ) {
        task.setTitle(
                request.getTitle().trim()
        );

        task.setDescription(
                normalizeNullable(
                        request.getDescription()
                )
        );

        task.setAssignedTo(
                normalizeNullable(
                        request.getAssignedTo()
                )
        );

        task.setDueDate(
                request.getDueDate()
        );

        task.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : TaskStatus.TODO
        );

        task.setPriority(
                request.getPriority() != null
                        ? request.getPriority()
                        : TaskPriority.MEDIUM
        );
    }

    private WeddingTask getTaskEntity(
            Long taskId,
            Integer userId
    ) {
        return weddingTaskRepo
                .findByIdAndUserId(
                        taskId,
                        userId
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Wedding task not found with id: "
                                        + taskId
                        )
                );
    }

    private User getUserById(
            Integer userId
    ) {
        return userRepo
                .findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User not found with id: "
                                        + userId
                        )
                );
    }

    private Integer getCurrentUserId() {

        Integer userId =
                currentUserService
                        .getCurrentUserId();

        if (userId == null) {
            throw new IllegalStateException(
                    "Current user not found"
            );
        }

        return userId;
    }

    private WeddingTaskResponse mapToResponse(
            WeddingTask task
    ) {
        return WeddingTaskResponse
                .builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(
                        task.getDescription()
                )
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .assignedTo(
                        task.getAssignedTo()
                )
                .createdAt(
                        task.getCreatedAt()
                )
                .updatedAt(
                        task.getUpdatedAt()
                )
                .build();
    }

    private String normalize(
            String value
    ) {
        return value == null
                ? ""
                : value.trim();
    }

    private String normalizeNullable(
            String value
    ) {
        if (
                value == null
                        || value.isBlank()
        ) {
            return null;
        }

        return value.trim();
    }
}

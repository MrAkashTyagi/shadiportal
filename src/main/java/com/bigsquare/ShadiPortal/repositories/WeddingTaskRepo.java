package com.bigsquare.ShadiPortal.repositories;

import com.bigsquare.ShadiPortal.entities.WeddingTask;
import com.bigsquare.ShadiPortal.enums.TaskPriority;
import com.bigsquare.ShadiPortal.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WeddingTaskRepo
        extends JpaRepository<WeddingTask, Long> {

    Optional<WeddingTask> findByIdAndUserId(
            Long taskId,
            Integer userId
    );

    @Query("""
            SELECT task
            FROM WeddingTask task
            WHERE task.user.id = :userId
            AND (
                :search = ''
                OR LOWER(task.title)
                    LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(COALESCE(task.description, ''))
                    LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(COALESCE(task.assignedTo, ''))
                    LIKE LOWER(CONCAT('%', :search, '%'))
            )
            AND (
                :status IS NULL
                OR task.status = :status
            )
            AND (
                :priority IS NULL
                OR task.priority = :priority
            )
            """)
    Page<WeddingTask> findTasksWithFilters(
            @Param("userId")
            Integer userId,

            @Param("search")
            String search,

            @Param("status")
            TaskStatus status,

            @Param("priority")
            TaskPriority priority,

            Pageable pageable
    );

    long countByUserIdAndStatus(
            Integer userId,
            TaskStatus status
    );
}

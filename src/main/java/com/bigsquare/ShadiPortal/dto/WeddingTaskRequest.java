package com.bigsquare.ShadiPortal.dto;

import com.bigsquare.ShadiPortal.enums.TaskPriority;
import com.bigsquare.ShadiPortal.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeddingTaskRequest {

    private String title;

    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private LocalDate dueDate;

    private String assignedTo;
}

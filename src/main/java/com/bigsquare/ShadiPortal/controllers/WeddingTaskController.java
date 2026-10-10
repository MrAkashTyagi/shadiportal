package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.dto.WeddingTaskRequest;
import com.bigsquare.ShadiPortal.dto.WeddingTaskResponse;
import com.bigsquare.ShadiPortal.enums.TaskPriority;
import com.bigsquare.ShadiPortal.enums.TaskStatus;
import com.bigsquare.ShadiPortal.services.WeddingTaskService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wedding-tasks")
@CrossOrigin(origins = "http://localhost:4200")
public class WeddingTaskController {

    private final WeddingTaskService weddingTaskService;

    public WeddingTaskController(
            WeddingTaskService weddingTaskService
    ) {
        this.weddingTaskService =
                weddingTaskService;
    }

    @PostMapping
    public ResponseEntity<WeddingTaskResponse>
    createTask(
            @RequestBody
            WeddingTaskRequest request
    ) {
        return ResponseEntity.ok(
                weddingTaskService.createTask(
                        request
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<WeddingTaskResponse>
    updateTask(
            @PathVariable
            Long id,

            @RequestBody
            WeddingTaskRequest request
    ) {
        return ResponseEntity.ok(
                weddingTaskService.updateTask(
                        id,
                        request
                )
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<WeddingTaskResponse>
    updateTaskStatus(
            @PathVariable
            Long id,

            @RequestParam
            TaskStatus status
    ) {
        return ResponseEntity.ok(
                weddingTaskService
                        .updateTaskStatus(
                                id,
                                status
                        )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<WeddingTaskResponse>
    getTaskById(
            @PathVariable
            Long id
    ) {
        return ResponseEntity.ok(
                weddingTaskService
                        .getTaskById(id)
        );
    }

    @GetMapping
    public ResponseEntity<Page<WeddingTaskResponse>>
    getTasks(
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size,

            @RequestParam(defaultValue = "")
            String search,

            @RequestParam(required = false)
            TaskStatus status,

            @RequestParam(required = false)
            TaskPriority priority
    ) {
        return ResponseEntity.ok(
                weddingTaskService.getTasks(
                        page,
                        size,
                        search,
                        status,
                        priority
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable
            Long id
    ) {
        weddingTaskService.deleteTask(id);

        return ResponseEntity.noContent()
                .build();
    }
}

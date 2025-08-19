package com.catch_ya_group.catch_ya.controller.status;

import com.catch_ya_group.catch_ya.modal.entity.InstanceStatus;
import com.catch_ya_group.catch_ya.service.status.InstanceStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/private/v1/instance")
@RequiredArgsConstructor
@Tag(
        name = "Instance Status",
        description = "APIs to manage and query instance statuses for users"
)
public class InstanceStatusController {

    private final InstanceStatusService instanceStatusService;

    @Operation(
            summary = "Get instance status by user ID",
            description = "Retrieve the current instance status information for a specific user using their user ID."
    )
    @GetMapping("/{userId}")
    public ResponseEntity<?> getInstanceStatusByUserId(@PathVariable Long userId){
        return ResponseEntity.ok(instanceStatusService.getInstanceStatusByUserId(userId));
    }

    @Operation(
            summary = "Create a new instance status",
            description = "Create and store a new instance status entry. "
                    + "The request body must contain a valid `InstanceStatus` object."
    )
    @PostMapping
    public ResponseEntity<?> createInstanceStatus(@RequestBody InstanceStatus instanceStatus){
        return ResponseEntity.ok(instanceStatusService.createInstanceStatus(instanceStatus));
    }

    @Operation(
            summary = "Get all instances",
            description = "Retrieve a list of all instance status records available in the system."
    )
    @GetMapping
    public ResponseEntity<?> getAllInstance(){
        return ResponseEntity.ok(instanceStatusService.getAllInstance());
    }

    @Operation(
            summary = "Delete instance status by user ID",
            description = "Delete the current instance status information for a specific user using their user ID."
    )
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteInstanceStatus(@PathVariable Long userId){
        return ResponseEntity.ok(instanceStatusService.deleteInstanceStatus(userId));
    }
}

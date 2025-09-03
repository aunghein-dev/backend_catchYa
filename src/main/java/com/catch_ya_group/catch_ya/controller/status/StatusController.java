package com.catch_ya_group.catch_ya.controller.status;

import com.catch_ya_group.catch_ya.modal.dto.StatusCreateRequest;
import com.catch_ya_group.catch_ya.modal.entity.Status;
import com.catch_ya_group.catch_ya.service.status.StatusService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/private/v1/statuses")
@RequiredArgsConstructor
@Tag(
        name = "Content Status",
        description = "CRUD and search operations for Status entity"
)
public class StatusController {

    private final StatusService statusService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "Get all statuses", description = "Retrieve all statuses from the system")
    @GetMapping("/all")
    public ResponseEntity<List<Status>> getAll() {
        return ResponseEntity.ok(statusService.getAll());
    }

    @Operation(summary = "Get status by ID", description = "Retrieve a single status by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<Status> getById(@PathVariable Long id) {
        return ResponseEntity.ok(statusService.getById(id));
    }

    @Operation(summary = "Get statuses by user ID", description = "Retrieve all statuses created by a specific user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Status>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(statusService.getByUserId(userId));
    }

    @Operation(summary = "Search statuses by content", description = "Search for statuses containing specific content keywords")
    @GetMapping("/search/content")
    public ResponseEntity<List<Status>> searchByContent(@RequestParam String keyword) {
        return ResponseEntity.ok(statusService.searchByContent(keyword));
    }

    @Operation(summary = "Search statuses by keyword", description = "Search for statuses by hashKeywords")
    @GetMapping("/search/keyword")
    public ResponseEntity<List<Status>> searchByKeyword(@RequestParam String keyword) {
        return ResponseEntity.ok(statusService.searchByKeyword(keyword));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Status> create(
            @RequestPart("payload") String payloadJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws JsonProcessingException {
        StatusCreateRequest payload = objectMapper.readValue(payloadJson, StatusCreateRequest.class);
        Status saved = statusService.create(payload, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Status> update(
            @PathVariable Long id,
            @RequestPart("payload") String payloadJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(name = "merge", defaultValue = "false") boolean merge,
            @RequestParam(name = "clearImages", defaultValue = "false") boolean clearImages
    ) throws JsonProcessingException {
        StatusCreateRequest payload = objectMapper.readValue(payloadJson, StatusCreateRequest.class);
        Status saved = statusService.update(id, payload, images, merge, clearImages);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Delete status", description = "Delete a status by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        statusService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

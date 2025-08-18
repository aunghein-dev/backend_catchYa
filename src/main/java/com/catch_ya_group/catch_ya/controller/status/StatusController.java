package com.catch_ya_group.catch_ya.controller.status;

import com.catch_ya_group.catch_ya.modal.entity.Status;
import com.catch_ya_group.catch_ya.service.status.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/private/v1/statuses")
@RequiredArgsConstructor
public class StatusController {

    private final StatusService statusService;

    @GetMapping("/all")
    public List<Status> getAll() {
        return statusService.getAll();
    }

    @GetMapping("/{id}")
    public Status getById(@PathVariable Long id) {
        return statusService.getById(id);
    }

    @GetMapping("/user/{userId}")
    public List<Status> getByUser(@PathVariable Long userId) {
        return statusService.getByUserId(userId);
    }

    @GetMapping("/search/content")
    public List<Status> searchByContent(@RequestParam String keyword) {
        return statusService.searchByContent(keyword);
    }

    @GetMapping("/search/keyword")
    public List<Status> searchByKeyword(@RequestParam String keyword) {
        return statusService.searchByKeyword(keyword);
    }

    @PostMapping
    public Status create(@RequestBody Status status) {
        return statusService.create(status);
    }

    @PutMapping("/{id}")
    public Status update(@PathVariable Long id, @RequestBody Status status) {
        return statusService.update(id, status);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        statusService.delete(id);
    }
}

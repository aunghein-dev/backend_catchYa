package com.catch_ya_group.catch_ya.service.status;

import com.catch_ya_group.catch_ya.modal.entity.Status;
import com.catch_ya_group.catch_ya.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;

    public List<Status> getAll() {
        return statusRepository.findAll();
    }

    public Status getById(Long id) {
        return statusRepository.findById(id).orElse(null);
    }

    public List<Status> getByUserId(Long userId) {
        return statusRepository.findByUserId(userId);
    }

    public List<Status> searchByContent(String keyword) {
        return statusRepository.findByContentContaining(keyword);
    }

    public List<Status> searchByKeyword(String keyword) {
        return statusRepository.findByKeyword(keyword);
    }

    public Status create(Status status) {
        return statusRepository.save(status);
    }

    public Status update(Long id, Status status) {
        Status existing = statusRepository.findById(id).orElseThrow();
        existing.setUserId(status.getUserId());
        existing.setContent(status.getContent());
        existing.setHashKeywords(status.getHashKeywords());
        existing.setImages(status.getImages());
        return statusRepository.save(existing);
    }

    public void delete(Long id) {
        statusRepository.deleteById(id);
    }
}

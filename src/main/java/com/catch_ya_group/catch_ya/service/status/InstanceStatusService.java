package com.catch_ya_group.catch_ya.service.status;

import com.catch_ya_group.catch_ya.modal.entity.InstanceStatus;
import com.catch_ya_group.catch_ya.repository.InstanceStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InstanceStatusService {

    private final InstanceStatusRepository instanceStatusRepository;

    public InstanceStatus getInstanceStatusByUserId(Long userId) {
        return instanceStatusRepository.getInstanceStatusByUserId(userId);
    }

    public InstanceStatus createInstanceStatus(InstanceStatus instanceStatus) {
        if(checkExistInstanceStatus(instanceStatus.getUserId())){
            InstanceStatus existingInstance = instanceStatusRepository.getInstanceStatusByUserId(instanceStatus.getUserId());
            existingInstance.setDayStatus(instanceStatus.getDayStatus());
            existingInstance.setStatusDate(new Date());
            instanceStatusRepository.save(existingInstance);
        }
        else {
            instanceStatus.setStatusDate(new Date());
            instanceStatusRepository.save(instanceStatus);
        }
        return instanceStatus;
    }

    public boolean checkExistInstanceStatus(Long userId){
        return instanceStatusRepository.checkExistInstanceStatus(userId);
    }

    public List<InstanceStatus> getAllInstance() {
        return instanceStatusRepository.findAll();
    }

    @Transactional
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void autoDeleteExpiredStatuses() {
        int deleted = instanceStatusRepository.deleteExpiredStatuses();
        if (deleted > 0) {
            System.out.println("Deleted " + deleted + " expired statuses");
        }
    }

    public InstanceStatus deleteInstanceStatus(Long userId) {
        InstanceStatus instanceStatus = instanceStatusRepository.getInstanceStatusByUserId(userId);
        if(instanceStatus!=null){
            instanceStatusRepository.delete(instanceStatus);
        }
        return instanceStatus;
    }
}

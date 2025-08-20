package com.catch_ya_group.catch_ya.service.linktree;

import com.catch_ya_group.catch_ya.modal.entity.LinkTree;
import com.catch_ya_group.catch_ya.repository.LinkTreeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkTreeService {

    private final LinkTreeRepository linkTreeRepository;

    public LinkTree getAllLinkTree(Long userId) {
        return linkTreeRepository.findByUserId(userId);
    }

    public LinkTree createOrUpdateLinkTree(LinkTree linkTree) {
        if (!linkTreeRepository.existsByUserId(linkTree.getUserId())) {
            return linkTreeRepository.save(linkTree);
        } else {
            LinkTree existing = linkTreeRepository.findByUserId(linkTree.getUserId());
            existing.setFacebookUrl(linkTree.getFacebookUrl());
            existing.setInstagramUrl(linkTree.getInstagramUrl());
            existing.setTelegramUrl(linkTree.getTelegramUrl());
            existing.setTiktokUrl(linkTree.getTiktokUrl());
            return linkTreeRepository.save(existing);
        }
    }

}

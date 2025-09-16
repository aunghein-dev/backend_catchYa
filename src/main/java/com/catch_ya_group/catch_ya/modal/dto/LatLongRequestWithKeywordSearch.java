package com.catch_ya_group.catch_ya.modal.dto;

public record LatLongRequestWithKeywordSearch(
        String keyword,
        Double latitude,
        Double longitude
) {}
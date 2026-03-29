package com.example.shortlink.dto;

import lombok.Data;

@Data
public class CreateShortLinkRequest {
    private String url;
    private String customSuffix;
    private Long userId;
    private Integer expireDays;
}
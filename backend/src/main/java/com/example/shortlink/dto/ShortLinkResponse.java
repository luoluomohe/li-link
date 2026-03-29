package com.example.shortlink.dto;

import lombok.Data;

@Data
public class ShortLinkResponse {
    private Long id;
    private String shortCode;
    private String shortUrl;
    private String originalUrl;
    private Long userId;
    private Integer status;
    private Integer clickCount;
    private String createTime;
    private String expireTime;
}
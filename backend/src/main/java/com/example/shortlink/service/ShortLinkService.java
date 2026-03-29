package com.example.shortlink.service;

import com.example.shortlink.dto.CreateShortLinkRequest;
import com.example.shortlink.dto.LinkStatsDTO;
import com.example.shortlink.dto.ShortLinkResponse;

import java.util.List;

public interface ShortLinkService {
    ShortLinkResponse createShortLink(CreateShortLinkRequest request);

    String getOriginalUrl(String shortCode);

    List<ShortLinkResponse> getUserLinks(Long userId);

    ShortLinkResponse getLinkById(Long linkId);

    void updateLinkStatus(Long linkId, Integer status);

    void deleteLink(Long linkId);

    void restoreLink(Long linkId);

    LinkStatsDTO getLinkStats(Long linkId, String period);

    void recordAccess(String shortCode, String ip, String device, String browser, String os, String referer);

    List<ShortLinkResponse> getAllLinks(Long userId, String domain, Integer status, int page, int size);

    Long getTotalLinks();

    Long getTotalClicks();

    Long getTodayClicks();

    Long getUserLinkCountInPeriod(Long userId, String period);
}
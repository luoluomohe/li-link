package com.example.shortlink.controller;

import com.example.shortlink.common.Result;
import com.example.shortlink.dto.CreateShortLinkRequest;
import com.example.shortlink.dto.ShortLinkResponse;
import com.example.shortlink.service.ShortLinkService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
public class ShortLinkController {

    @Autowired
    private ShortLinkService shortLinkService;

    @PostMapping("/api/shortlink/create")
    public Result<ShortLinkResponse> createShortLink(@RequestBody CreateShortLinkRequest request) {
        if (request.getUserId() == null) {
            String anonymousId = UUID.randomUUID().toString();
            request.setUserId(anonymousId.hashCode() & 0x7FFFFFFFL);
        }
        ShortLinkResponse response = shortLinkService.createShortLink(request);
        return Result.success(response);
    }

    @GetMapping("/{shortCode}")
    public void redirectToOriginal(@PathVariable String shortCode, HttpServletResponse response) {
        String originalUrl = shortLinkService.getOriginalUrl(shortCode);

        if (originalUrl != null) {
            String ip = getClientIp();
            String device = getDeviceType();
            String browser = getBrowserType();
            String os = getOsType();

            shortLinkService.recordAccess(shortCode, ip, device, browser, os, null);

            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", originalUrl);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            try {
                response.getWriter().write("Short link not found");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping("/api/shortlink/user/{userId}")
    public Result<List<ShortLinkResponse>> getUserLinks(@PathVariable Long userId) {
        List<ShortLinkResponse> links = shortLinkService.getUserLinks(userId);
        return Result.success(links);
    }

    @GetMapping("/api/shortlink/{id}")
    public Result<ShortLinkResponse> getLinkById(@PathVariable Long id) {
        ShortLinkResponse response = shortLinkService.getLinkById(id);
        return Result.success(response);
    }

    @PutMapping("/api/shortlink/{id}/status")
    public Result<Void> updateLinkStatus(@PathVariable Long id, @RequestParam Integer status) {
        shortLinkService.updateLinkStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/api/shortlink/{id}")
    public Result<Void> deleteLink(@PathVariable Long id) {
        shortLinkService.deleteLink(id);
        return Result.success();
    }

    @GetMapping("/api/shortlink/{id}/stats")
    public Result<com.example.shortlink.dto.LinkStatsDTO> getLinkStats(@PathVariable Long id, @RequestParam(defaultValue = "7") String period) {
        com.example.shortlink.dto.LinkStatsDTO stats = shortLinkService.getLinkStats(id, period);
        return Result.success(stats);
    }

    private String getClientIp() {
        return "127.0.0.1";
    }

    private String getDeviceType() {
        return "PC";
    }

    private String getBrowserType() {
        return "Unknown";
    }

    private String getOsType() {
        return "Unknown";
    }
}
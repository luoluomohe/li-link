package com.example.shortlink.controller;

import com.example.shortlink.common.Result;
import com.example.shortlink.dto.ShortLinkResponse;
import com.example.shortlink.dto.StatsResponse;
import com.example.shortlink.service.ShortLinkService;
import com.example.shortlink.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/links")
public class AdminLinkController {

    @Autowired
    private ShortLinkService shortLinkService;

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping
    public Result<Map<String, Object>> getAllLinks(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<ShortLinkResponse> links = shortLinkService.getAllLinks(userId, domain, status, page, size);
        Long total = shortLinkService.getTotalLinks();

        Map<String, Object> result = new HashMap<>();
        result.put("links", links);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return Result.success(result);
    }

    @GetMapping("/stats")
    public Result<StatsResponse> getStats() {
        StatsResponse stats = new StatsResponse();
        stats.setTotalLinks(shortLinkService.getTotalLinks());
        stats.setTotalClicks(shortLinkService.getTotalClicks());
        stats.setTodayClicks(shortLinkService.getTodayClicks());
        stats.setTodayLinks(0L);
        return Result.success(stats);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateLinkStatus(@PathVariable Long id, @RequestParam Integer status) {
        shortLinkService.updateLinkStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteLink(@PathVariable Long id) {
        shortLinkService.deleteLink(id);
        return Result.success();
    }
}
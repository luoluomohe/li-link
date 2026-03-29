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

/**
 * 短链接控制器
 * 提供短链接的创建、跳转、查询、删除等REST API接口
 */
@RestController
public class ShortLinkController {

    @Autowired
    private ShortLinkService shortLinkService;

    /**
     * 创建短链接
     * 如果用户未登录，则生成匿名用户ID
     */
    @PostMapping("/api/shortlink/create")
    public Result<ShortLinkResponse> createShortLink(@RequestBody CreateShortLinkRequest request) {
        // 处理匿名用户，生成临时用户ID
        if (request.getUserId() == null) {
            String anonymousId = UUID.randomUUID().toString();
            request.setUserId(anonymousId.hashCode() & 0x7FFFFFFFL);
        }
        ShortLinkResponse response = shortLinkService.createShortLink(request);
        return Result.success(response);
    }

    /**
     * 短链接跳转
     * 根据短码查询原始URL并重定向，同时记录访问日志
     */
    @GetMapping("/{shortCode}")
    public void redirectToOriginal(@PathVariable String shortCode, 
                                   HttpServletRequest httpRequest,
                                   HttpServletResponse response) {
        String originalUrl = shortLinkService.getOriginalUrl(shortCode);

        if (originalUrl != null) {
            // 获取访问者信息
            String ip = getClientIp(httpRequest);
            String device = getDeviceType(httpRequest);
            String browser = getBrowserType(httpRequest);
            String os = getOsType(httpRequest);

            // 记录访问日志
            shortLinkService.recordAccess(shortCode, ip, device, browser, os, null);

            // 302重定向到原始URL
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", originalUrl);
        } else {
            // 短链接不存在，返回404
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            try {
                response.getWriter().write("Short link not found");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取用户的短链接列表
     */
    @GetMapping("/api/shortlink/user/{userId}")
    public Result<List<ShortLinkResponse>> getUserLinks(@PathVariable Long userId) {
        List<ShortLinkResponse> links = shortLinkService.getUserLinks(userId);
        return Result.success(links);
    }

    /**
     * 根据ID获取短链接详情
     */
    @GetMapping("/api/shortlink/{id}")
    public Result<ShortLinkResponse> getLinkById(@PathVariable Long id) {
        ShortLinkResponse response = shortLinkService.getLinkById(id);
        return Result.success(response);
    }

    /**
     * 更新短链接状态（启用/禁用）
     */
    @PutMapping("/api/shortlink/{id}/status")
    public Result<Void> updateLinkStatus(@PathVariable Long id, @RequestParam Integer status) {
        shortLinkService.updateLinkStatus(id, status);
        return Result.success();
    }

    /**
     * 删除短链接（软删除）
     */
    @DeleteMapping("/api/shortlink/{id}")
    public Result<Void> deleteLink(@PathVariable Long id) {
        shortLinkService.deleteLink(id);
        return Result.success();
    }

    /**
     * 恢复已删除的短链接
     */
    @PutMapping("/api/shortlink/{id}/restore")
    public Result<Void> restoreLink(@PathVariable Long id) {
        shortLinkService.restoreLink(id);
        return Result.success();
    }

    /**
     * 获取短链接统计数据
     */
    @GetMapping("/api/shortlink/{id}/stats")
    public Result<com.example.shortlink.dto.LinkStatsDTO> getLinkStats(@PathVariable Long id, @RequestParam(defaultValue = "7") String period) {
        com.example.shortlink.dto.LinkStatsDTO stats = shortLinkService.getLinkStats(id, period);
        return Result.success(stats);
    }

    /**
     * 获取客户端真实IP地址
     * 支持代理服务器场景
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip != null ? ip : "127.0.0.1";
    }

    /**
     * 根据User-Agent判断设备类型
     */
    private String getDeviceType(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Unknown";
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("mobile") || userAgent.contains("android") || userAgent.contains("iphone")) {
            return "Mobile";
        }
        if (userAgent.contains("tablet") || userAgent.contains("ipad")) {
            return "Tablet";
        }
        return "PC";
    }

    /**
     * 根据User-Agent判断浏览器类型
     */
    private String getBrowserType(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Unknown";
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("edge")) return "Edge";
        if (userAgent.contains("chrome")) return "Chrome";
        if (userAgent.contains("firefox")) return "Firefox";
        if (userAgent.contains("safari")) return "Safari";
        if (userAgent.contains("opera") || userAgent.contains("opr")) return "Opera";
        if (userAgent.contains("msie") || userAgent.contains("trident")) return "IE";
        return "Unknown";
    }

    /**
     * 根据User-Agent判断操作系统类型
     */
    private String getOsType(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Unknown";
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("windows")) return "Windows";
        if (userAgent.contains("mac")) return "MacOS";
        if (userAgent.contains("linux")) return "Linux";
        if (userAgent.contains("android")) return "Android";
        if (userAgent.contains("iphone") || userAgent.contains("ipad")) return "iOS";
        return "Unknown";
    }
}
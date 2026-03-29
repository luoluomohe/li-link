package com.example.shortlink.controller;

import com.example.shortlink.common.Result;
import com.example.shortlink.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/config")
public class AdminConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping
    public Result<Map<String, String>> getAllConfigs() {
        Map<String, String> configs = systemConfigService.getAllConfigs();
        return Result.success(configs);
    }

    @PutMapping
    public Result<Void> updateConfig(@RequestParam String key, @RequestParam String value) {
        systemConfigService.setConfig(key, value);
        return Result.success();
    }

    @GetMapping("/short-link-length")
    public Result<Map<String, Integer>> getShortLinkLength() {
        Map<String, Integer> result = new HashMap<>();
        result.put("shortLinkLength", systemConfigService.getShortLinkLength());
        return Result.success(result);
    }

    @GetMapping("/allow-custom-suffix")
    public Result<Map<String, Boolean>> isAllowCustomSuffix() {
        Map<String, Boolean> result = new HashMap<>();
        result.put("allowCustomSuffix", systemConfigService.isAllowCustomSuffix());
        return Result.success(result);
    }
}
package com.example.shortlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.shortlink.mapper.SystemConfigMapper;
import com.example.shortlink.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CONFIG_KEY_PREFIX = "system:config:";

    @Override
    public String getConfig(String key) {
        String redisKey = CONFIG_KEY_PREFIX + key;
        Object cachedValue = redisTemplate.opsForValue().get(redisKey);
        if (cachedValue != null) {
            return cachedValue.toString();
        }

        LambdaQueryWrapper<com.example.shortlink.entity.SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.example.shortlink.entity.SystemConfig::getConfigKey, key);
        com.example.shortlink.entity.SystemConfig config = systemConfigMapper.selectOne(wrapper);

        if (config != null) {
            redisTemplate.opsForValue().set(redisKey, config.getConfigValue(), 1, TimeUnit.HOURS);
            return config.getConfigValue();
        }
        return null;
    }

    @Override
    public String getConfig(String key, String defaultValue) {
        String value = getConfig(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public void setConfig(String key, String value) {
        LambdaQueryWrapper<com.example.shortlink.entity.SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.example.shortlink.entity.SystemConfig::getConfigKey, key);
        com.example.shortlink.entity.SystemConfig config = systemConfigMapper.selectOne(wrapper);

        if (config != null) {
            config.setConfigValue(value);
            config.setUpdateTime(LocalDateTime.now());
            systemConfigMapper.updateById(config);
        } else {
            config = new com.example.shortlink.entity.SystemConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            config.setCreateTime(LocalDateTime.now());
            config.setUpdateTime(LocalDateTime.now());
            systemConfigMapper.insert(config);
        }

        String redisKey = CONFIG_KEY_PREFIX + key;
        redisTemplate.delete(redisKey);
    }

    @Override
    public Map<String, String> getAllConfigs() {
        List<com.example.shortlink.entity.SystemConfig> configs = systemConfigMapper.selectList(null);
        Map<String, String> result = new HashMap<>();
        for (com.example.shortlink.entity.SystemConfig config : configs) {
            result.put(config.getConfigKey(), config.getConfigValue());
        }
        return result;
    }

    @Override
    public Integer getShortLinkLength() {
        String length = getConfig("short_link_length", "6");
        return Integer.parseInt(length);
    }

    @Override
    public boolean isAllowCustomSuffix() {
        String allowed = getConfig("allow_custom_suffix", "true");
        return "true".equalsIgnoreCase(allowed);
    }

    @Override
    public Integer getDefaultExpireDays() {
        String days = getConfig("default_expire_days", "30");
        int value = Integer.parseInt(days);
        if (value < 1) value = 1;
        if (value > 3650) value = 3650;
        return value;
    }

    @Override
    public boolean isLimitEnabled() {
        String enabled = getConfig("limit_enabled", "false");
        return "true".equalsIgnoreCase(enabled);
    }

    @Override
    public Integer getLimitCount() {
        String count = getConfig("limit_count", "10");
        int value = Integer.parseInt(count);
        if (value < 1) value = 1;
        return value;
    }

    @Override
    public String getLimitPeriod() {
        return getConfig("limit_period", "day");
    }

    @Override
    public String getThemePrimaryColor() {
        return getConfig("theme_primary_color", "#6366f1");
    }

    @Override
    public String getThemeStyle() {
        return getConfig("theme_style", "default");
    }
}
package com.example.shortlink.service;

import java.util.Map;

public interface SystemConfigService {
    String getConfig(String key);

    String getConfig(String key, String defaultValue);

    void setConfig(String key, String value);

    Map<String, String> getAllConfigs();

    Integer getShortLinkLength();

    boolean isAllowCustomSuffix();

    Integer getDefaultExpireDays();

    boolean isLimitEnabled();

    Integer getLimitCount();

    String getLimitPeriod();

    String getThemePrimaryColor();

    String getThemeStyle();
}
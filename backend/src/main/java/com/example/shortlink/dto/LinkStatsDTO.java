package com.example.shortlink.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class LinkStatsDTO {
    private Long linkId;
    private String shortCode;
    private Long totalPv;
    private Long totalUv;
    private List<Map<String, Object>> dailyTrend;
    private List<Map<String, Object>> hourlyTrend;
    private Map<String, Long> deviceDistribution;
    private Map<String, Long> browserDistribution;
    private Map<String, Long> countryDistribution;
}
package com.example.shortlink.dto;

import lombok.Data;
import java.util.Map;

@Data
public class StatsResponse {
    private Long totalLinks;
    private Long totalClicks;
    private Long todayClicks;
    private Long todayLinks;
    private Map<String, Long> deviceDistribution;
    private Map<String, Long> browserDistribution;
}
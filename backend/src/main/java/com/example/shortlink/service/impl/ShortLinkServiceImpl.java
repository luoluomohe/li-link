package com.example.shortlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.shortlink.dto.CreateShortLinkRequest;
import com.example.shortlink.dto.LinkStatsDTO;
import com.example.shortlink.dto.ShortLinkResponse;
import com.example.shortlink.entity.LinkStats;
import com.example.shortlink.entity.ShortLink;
import com.example.shortlink.mapper.LinkStatsMapper;
import com.example.shortlink.mapper.ShortLinkMapper;
import com.example.shortlink.service.ShortLinkService;
import com.example.shortlink.service.SystemConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ShortLinkServiceImpl implements ShortLinkService {

    @Autowired
    private ShortLinkMapper shortLinkMapper;

    @Autowired
    private LinkStatsMapper linkStatsMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String SHORT_LINK_KEY_PREFIX = "short:link:";
    private static final String SHORT_CODE_KEY_PREFIX = "short:code:";
    private static final String CLICK_COUNT_KEY_PREFIX = "short:click:count:";

    @Override
    @Transactional
    public ShortLinkResponse createShortLink(CreateShortLinkRequest request) {
        String shortCode;
        boolean allowCustom = systemConfigService.isAllowCustomSuffix();

        if (request.getCustomSuffix() != null && !request.getCustomSuffix().isEmpty() && allowCustom) {
            shortCode = request.getCustomSuffix();
            if (isShortCodeExists(shortCode)) {
                shortCode = generateUniqueShortCode();
            }
        } else {
            shortCode = generateUniqueShortCode();
        }

        ShortLink shortLink = new ShortLink();
        shortLink.setShortCode(shortCode);
        shortLink.setOriginalUrl(request.getUrl());
        shortLink.setUserId(request.getUserId());
        shortLink.setDomain("localhost:8080");
        shortLink.setStatus(1);
        shortLink.setClickCount(0);
        shortLink.setCreateTime(LocalDateTime.now());
        shortLink.setExpireTime(LocalDateTime.now().plusYears(1));

        shortLinkMapper.insert(shortLink);

        String redisKey = SHORT_CODE_KEY_PREFIX + shortCode;
        redisTemplate.opsForValue().set(redisKey, request.getUrl(), 24, TimeUnit.HOURS);

        String linkRedisKey = SHORT_LINK_KEY_PREFIX + shortLink.getId();
        try {
            redisTemplate.opsForValue().set(linkRedisKey, objectMapper.writeValueAsString(shortLink), 24, TimeUnit.HOURS);
        } catch (Exception e) {
            // Redis缓存失败不影响主流程，记录日志即可
            e.printStackTrace();
        }

        return convertToResponse(shortLink);
    }

    @Override
    public String getOriginalUrl(String shortCode) {
        String redisKey = SHORT_CODE_KEY_PREFIX + shortCode;
        Object cachedUrl = redisTemplate.opsForValue().get(redisKey);

        if (cachedUrl != null) {
            return cachedUrl.toString();
        }

        LambdaQueryWrapper<ShortLink> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShortLink::getShortCode, shortCode);
        ShortLink shortLink = shortLinkMapper.selectOne(wrapper);

        if (shortLink != null && shortLink.getStatus() == 1) {
            redisTemplate.opsForValue().set(redisKey, shortLink.getOriginalUrl(), 24, TimeUnit.HOURS);
            return shortLink.getOriginalUrl();
        }

        return null;
    }

    @Override
    public List<ShortLinkResponse> getUserLinks(Long userId) {
        LambdaQueryWrapper<ShortLink> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShortLink::getUserId, userId);
        wrapper.orderByDesc(ShortLink::getCreateTime);
        List<ShortLink> links = shortLinkMapper.selectList(wrapper);
        return links.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public ShortLinkResponse getLinkById(Long linkId) {
        ShortLink shortLink = shortLinkMapper.selectById(linkId);
        return shortLink != null ? convertToResponse(shortLink) : null;
    }

    @Override
    @Transactional
    public void updateLinkStatus(Long linkId, Integer status) {
        ShortLink shortLink = shortLinkMapper.selectById(linkId);
        if (shortLink != null) {
            shortLink.setStatus(status);
            shortLink.setUpdateTime(LocalDateTime.now());
            shortLinkMapper.updateById(shortLink);

            String linkRedisKey = SHORT_LINK_KEY_PREFIX + linkId;
            redisTemplate.delete(linkRedisKey);
        }
    }

    @Override
    @Transactional
    public void deleteLink(Long linkId) {
        ShortLink shortLink = shortLinkMapper.selectById(linkId);
        if (shortLink != null) {
            shortLinkMapper.deleteById(linkId);

            String linkRedisKey = SHORT_LINK_KEY_PREFIX + linkId;
            String codeRedisKey = SHORT_CODE_KEY_PREFIX + shortLink.getShortCode();
            redisTemplate.delete(linkRedisKey);
            redisTemplate.delete(codeRedisKey);
        }
    }

    @Override
    public LinkStatsDTO getLinkStats(Long linkId, String period) {
        ShortLink shortLink = shortLinkMapper.selectById(linkId);
        if (shortLink == null) {
            return null;
        }

        LinkStatsDTO statsDTO = new LinkStatsDTO();
        statsDTO.setLinkId(linkId);
        statsDTO.setShortCode(shortLink.getShortCode());

        LambdaQueryWrapper<LinkStats> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LinkStats::getLinkId, linkId);

        List<LinkStats> allStats = linkStatsMapper.selectList(wrapper);

        statsDTO.setTotalPv((long) allStats.size());
        statsDTO.setTotalUv(allStats.stream().map(LinkStats::getIp).distinct().count());

        Map<String, Long> deviceDist = allStats.stream()
                .collect(Collectors.groupingBy(s -> s.getDevice() != null ? s.getDevice() : "Unknown", Collectors.counting()));
        statsDTO.setDeviceDistribution(deviceDist);

        Map<String, Long> browserDist = allStats.stream()
                .collect(Collectors.groupingBy(s -> s.getBrowser() != null ? s.getBrowser() : "Unknown", Collectors.counting()));
        statsDTO.setBrowserDistribution(browserDist);

        Map<String, Long> countryDist = allStats.stream()
                .collect(Collectors.groupingBy(s -> s.getCountry() != null ? s.getCountry() : "Unknown", Collectors.counting()));
        statsDTO.setCountryDistribution(countryDist);

        return statsDTO;
    }

    @Override
    @Async
    public void recordAccess(String shortCode, String ip, String device, String browser, String os, String referer) {
        try {
            LambdaQueryWrapper<ShortLink> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ShortLink::getShortCode, shortCode);
            ShortLink shortLink = shortLinkMapper.selectOne(wrapper);

            if (shortLink == null) {
                return;
            }

            LinkStats stats = new LinkStats();
            stats.setLinkId(shortLink.getId());
            stats.setShortCode(shortCode);
            stats.setIp(ip);
            stats.setDevice(device);
            stats.setBrowser(browser);
            stats.setOs(os);
            stats.setReferer(referer);
            stats.setAccessTime(LocalDateTime.now());

            linkStatsMapper.insert(stats);

            String clickCountKey = CLICK_COUNT_KEY_PREFIX + shortLink.getId();
            redisTemplate.opsForValue().increment(clickCountKey);

            Long currentCount = redisTemplate.opsForValue().increment(CLICK_COUNT_KEY_PREFIX + "total");
            if (currentCount != null && currentCount % 100 == 0) {
                syncClickCountToDb(shortLink.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ShortLinkResponse> getAllLinks(Long userId, String domain, Integer status, int page, int size) {
        LambdaQueryWrapper<ShortLink> wrapper = new LambdaQueryWrapper<>();

        if (userId != null) {
            wrapper.eq(ShortLink::getUserId, userId);
        }
        if (domain != null && !domain.isEmpty()) {
            wrapper.eq(ShortLink::getDomain, domain);
        }
        if (status != null) {
            wrapper.eq(ShortLink::getStatus, status);
        }

        wrapper.orderByDesc(ShortLink::getCreateTime);
        wrapper.last("LIMIT " + size + " OFFSET " + (page - 1) * size);

        List<ShortLink> links = shortLinkMapper.selectList(wrapper);
        return links.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public Long getTotalLinks() {
        return shortLinkMapper.selectCount(null);
    }

    @Override
    public Long getTotalClicks() {
        Object cachedCount = redisTemplate.opsForValue().get(CLICK_COUNT_KEY_PREFIX + "total");
        if (cachedCount != null) {
            return Long.parseLong(cachedCount.toString());
        }
        Long totalClicks = linkStatsMapper.selectCount(null);
        redisTemplate.opsForValue().set(CLICK_COUNT_KEY_PREFIX + "total", totalClicks);
        return totalClicks;
    }

    @Override
    public Long getTodayClicks() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LambdaQueryWrapper<LinkStats> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(LinkStats::getAccessTime, startOfDay);
        return linkStatsMapper.selectCount(wrapper);
    }

    private String generateUniqueShortCode() {
        int length = systemConfigService.getShortLinkLength();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        String shortCode;

        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            shortCode = sb.toString();
        } while (isShortCodeExists(shortCode));

        return shortCode;
    }

    private boolean isShortCodeExists(String shortCode) {
        String redisKey = SHORT_CODE_KEY_PREFIX + shortCode;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            return true;
        }

        LambdaQueryWrapper<ShortLink> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShortLink::getShortCode, shortCode);
        return shortLinkMapper.selectCount(wrapper) > 0;
    }

    private void syncClickCountToDb(Long linkId) {
        try {
            String clickCountKey = CLICK_COUNT_KEY_PREFIX + linkId;
            Object count = redisTemplate.opsForValue().get(clickCountKey);
            if (count != null) {
                ShortLink shortLink = shortLinkMapper.selectById(linkId);
                if (shortLink != null) {
                    shortLink.setClickCount(Integer.parseInt(count.toString()));
                    shortLink.setUpdateTime(LocalDateTime.now());
                    shortLinkMapper.updateById(shortLink);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ShortLinkResponse convertToResponse(ShortLink shortLink) {
        ShortLinkResponse response = new ShortLinkResponse();
        response.setId(shortLink.getId());
        response.setShortCode(shortLink.getShortCode());
        response.setShortUrl("http://localhost:8080/" + shortLink.getShortCode());
        response.setOriginalUrl(shortLink.getOriginalUrl());
        response.setUserId(shortLink.getUserId());
        response.setStatus(shortLink.getStatus());
        response.setClickCount(shortLink.getClickCount());
        response.setCreateTime(shortLink.getCreateTime() != null ? shortLink.getCreateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
        response.setExpireTime(shortLink.getExpireTime() != null ? shortLink.getExpireTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
        return response;
    }
}
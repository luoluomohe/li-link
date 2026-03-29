package com.example.shortlink.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_link_stats")
public class LinkStats {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long linkId;
    private String shortCode;
    private String ip;
    private String country;
    private String province;
    private String city;
    private String device;
    private String browser;
    private String os;
    private String referer;
    private LocalDateTime accessTime;
}
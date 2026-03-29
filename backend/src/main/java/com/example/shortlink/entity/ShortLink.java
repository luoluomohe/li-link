package com.example.shortlink.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_short_link")
public class ShortLink {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String shortCode;
    private String originalUrl;
    private Long userId;
    private String domain;
    private Integer status;
    private Integer clickCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime expireTime;
}
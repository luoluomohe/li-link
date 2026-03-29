-- 创建数据库
CREATE DATABASE IF NOT EXISTS shortlink DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE shortlink;

-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    status INT DEFAULT 1 COMMENT '1:启用 0:禁用',
    is_admin INT DEFAULT 0 COMMENT '1:管理员 0:普通用户',
    session_token VARCHAR(100),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 短链接表
CREATE TABLE IF NOT EXISTS t_short_link (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    short_code VARCHAR(20) NOT NULL UNIQUE COMMENT '短链码',
    original_url TEXT NOT NULL COMMENT '原始链接',
    user_id BIGINT,
    domain VARCHAR(100) DEFAULT 'localhost' COMMENT '域名',
    status INT DEFAULT 1 COMMENT '1:启用 0:禁用',
    click_count INT DEFAULT 0 COMMENT '点击次数',
    deleted INT DEFAULT 0 COMMENT '是否删除 0:否 1:是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    expire_time DATETIME COMMENT '过期时间',
    INDEX idx_short_code (short_code),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接表';

-- 链接访问统计表
CREATE TABLE IF NOT EXISTS t_link_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    link_id BIGINT NOT NULL COMMENT '链接ID',
    short_code VARCHAR(20) NOT NULL COMMENT '短链码',
    ip VARCHAR(50) COMMENT 'IP地址',
    country VARCHAR(50) COMMENT '国家',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    device VARCHAR(50) COMMENT '设备类型',
    browser VARCHAR(50) COMMENT '浏览器',
    os VARCHAR(50) COMMENT '操作系统',
    referer VARCHAR(500) COMMENT '来源',
    access_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_link_id (link_id),
    INDEX idx_short_code (short_code),
    INDEX idx_access_time (access_time),
    INDEX idx_ip (ip)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='链接访问统计表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS t_system_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(50) NOT NULL UNIQUE COMMENT '配置键',
    config_value VARCHAR(500) COMMENT '配置值',
    description VARCHAR(200) COMMENT '配置描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 初始化管理员账户 (密码: admin123)
INSERT INTO t_user (username, password, email, status, is_admin)
VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', 'admin@example.com', 1, 1)
ON DUPLICATE KEY UPDATE username = username;

-- 初始化系统配置
INSERT INTO t_system_config (config_key, config_value, description)
VALUES
    ('short_link_length', '6', '短链长度'),
    ('allow_custom_suffix', 'true', '是否允许自定义后缀'),
    ('short_domain', 'http://localhost:8080', '短链域名'),
    ('default_expire_days', '30', '默认过期天数'),
    ('limit_enabled', 'false', '是否开启创建限制'),
    ('limit_count', '10', '限制数量'),
    ('limit_period', 'day', '限制周期 day/month/year')
ON DUPLICATE KEY UPDATE config_key = config_key;
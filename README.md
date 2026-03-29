# 短链系统

一个完整的短链系统，包含用户端（C端）和管理后台。

## 技术栈

### 后端
- Spring Boot 3.2.x
- Spring Security
- Spring Data JPA
- MyBatis Plus
- Redis
- MySQL 8.0+

### 前端
- Vue 3
- Vite
- Element Plus
- Axios
- Vue Router
- Pinia
- ECharts

## 功能特性

### C端（用户端）
- 无需登录生成短链（可选自定义后缀）
- 短链接跳转（302重定向）
- 访问统计（IP、设备、时间）
- 短链列表
- 统计图表（PV/UV、设备分布）

### 管理后台
- 管理员登录
- 用户管理
- 短链管理
- 全站统计
- 系统配置

## 快速开始

### 1. 环境要求
- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Redis

### 2. 初始化数据库
```bash
mysql -u root -p < database/init.sql
```

### 3. 启动后端
```bash
cd backend
mvn spring-boot:run
```

### 4. 启动前端
```bash
cd frontend
npm install
npm run dev
```

### 5. 访问
- 前端：http://localhost:5173
- 后端API：http://localhost:8080
- Swagger文档：http://localhost:8080/swagger-ui.html
- 默认管理员账号：admin / admin123
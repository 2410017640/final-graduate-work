# 智能租房系统（基于 Spring Boot + AI）

本科毕业设计项目：一个带 AI 能力的智能租房系统，支持房源发布/审核、标签、AI 找房、AI 问答、预约看房等。

## 技术栈
- 后端：Spring Boot 3.5.7 + MyBatis-Plus 3.5.12 + MySQL 8.4 + Hutool（JWT）+ spring-security-crypto（BCrypt）
- 前端：Vue 3 + Element Plus + Vite + Vue Router + axios
- AI：DeepSeek（OpenAI 兼容接口，可切换；未配 key 时自动降级为规则/词典匹配）

## 项目结构
```
fin/
├── smart-rent-server/          # 后端（Spring Boot）
│   ├── sql/                    # 建表脚本（init.sql / house.sql / tag.sql / knowledge.sql / question.sql / appointment.sql）
│   └── src/main/java/com/smartrent/
├── smart-rent-frontend/        # 前端（Vue3 + Element Plus）
│   └── src/views/              # 各页面
├── 测试问题记录.md              # 测试过程中发现的问题与解决方案
└── README.md
```

## 环境要求
- JDK 17+
- Maven 3.6+（或用 IDEA 内置）
- MySQL 8.x
- Node.js 18+（前端）

## 一、准备数据库
1. 启动 MySQL，创建数据库：
   ```sql
   CREATE DATABASE IF NOT EXISTS smart_rent DEFAULT CHARSET utf8mb4;
   ```
2. 依次执行 `smart-rent-server/sql/` 下的建表脚本（顺序无严格要求，建议按文件名）：
   ```bash
   mysql -uroot -p123456 smart_rent < smart-rent-server/sql/init.sql
   mysql -uroot -p123456 smart_rent < smart-rent-server/sql/house.sql
   mysql -uroot -p123456 smart_rent < smart-rent-server/sql/tag.sql
   mysql -uroot -p123456 smart_rent < smart-rent-server/sql/knowledge.sql
   mysql -uroot -p123456 smart_rent < smart-rent-server/sql/question.sql
   mysql -uroot -p123456 smart_rent < smart-rent-server/sql/appointment.sql
   ```
   > 数据库连接信息在 `smart-rent-server/src/main/resources/application.yml`（默认 root/123456，端口 3306），按需修改。

## 二、启动后端（端口 8081）
方式一（IDEA）：打开 `smart-rent-server`，直接运行 `SmartRentApplication` 主类。

方式二（命令行）：
```bash
cd smart-rent-server
mvn package -DskipTests
java -jar target/smart-rent-server-1.0.0.jar
```
> 首次启动会自动播种：管理员账号 admin、预置标签、租房知识库。
> 若 8081 被占用或运行时篡改，可加 `-Dserver.port=8081` 强制指定端口。

## 三、启动前端（端口 5173）
```bash
cd smart-rent-frontend
npm install          # 首次
npm run dev          # 启动后访问 http://localhost:5173
```
> 前端通过 Vite 代理把 `/api` 转发到 `http://localhost:8081`，避免跨域。

## 四、访问与登录
浏览器打开：**http://localhost:5173**

三端账号（密码均为 `123456`）：

| 角色 | 用户名 | 说明 |
|---|---|---|
| 管理员 | `admin` | 房源审核、标签管理、知识库管理 |
| 房东 | `landlord` | 发布/管理房源、回答问题、处理预约 |
| 租客 | `tenant` | 找房、AI 问答、预约看房 |

## 五、可选：启用真实 AI（DeepSeek）
默认不配 key，AI 功能走规则/词典降级。要启用真实大模型，设置环境变量：
```bash
export DEEPSEEK_API_KEY=sk-xxxx        # Linux/macOS
set DEEPSEEK_API_KEY=sk-xxxx           # Windows CMD
$env:DEEPSEEK_API_KEY="sk-xxxx"        # PowerShell
```
模型/地址在 `application.yml` 的 `ai.deepseek` 配置块，可切换为其它 OpenAI 兼容服务。

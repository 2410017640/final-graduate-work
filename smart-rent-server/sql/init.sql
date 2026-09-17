-- 智能租房系统数据库脚本
-- 数据库：smart_rent（utf8mb4）

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `role`        VARCHAR(20)  NOT NULL COMMENT '角色：LANDLORD房东/TENANT房客/ADMIN管理员',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
    `create_time` DATETIME     DEFAULT NULL COMMENT '注册时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

-- 说明：内置管理员账号（admin / 123456）由后端 AdminInitRunner
-- 在系统「首次启动」时自动创建，密码使用 BCrypt 加密，无需在本文件写死。
-- 所以这里只建表，不插入管理员数据。如果数据库里已有 user 表，本句不会重复创建。

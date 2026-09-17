-- 标签相关表（Phase 4）
-- 说明：本文件需要在 smart_rent 数据库中执行一次，建表即可，不会重复创建。

-- 标签字典表：系统预置 + 管理员可维护的房源标签
CREATE TABLE IF NOT EXISTS tag (
  id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  name       VARCHAR(50)  NOT NULL                COMMENT '标签名称（如：近地铁、精装修）',
  category   VARCHAR(20)  DEFAULT ''              COMMENT '分组（如：交通/户型/租约/设施/人群），仅用于前端展示归类',
  create_time DATETIME    DEFAULT NULL            COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_name (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '房源标签字典表';

-- 房源-标签 多对多关系表
CREATE TABLE IF NOT EXISTS house_tag (
  house_id   BIGINT       NOT NULL                COMMENT '房源ID（关联 house.id）',
  tag_id     BIGINT       NOT NULL                COMMENT '标签ID（关联 tag.id）',
  PRIMARY KEY (house_id, tag_id),
  KEY idx_tag (tag_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '房源-标签关系表';

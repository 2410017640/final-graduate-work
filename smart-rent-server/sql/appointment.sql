-- 预约看房表（Phase 12）

CREATE TABLE IF NOT EXISTS appointment (
  id               BIGINT      NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  house_id         BIGINT      NOT NULL                COMMENT '房源ID',
  tenant_id        BIGINT      NOT NULL                COMMENT '预约的租客ID',
  landlord_id      BIGINT      NOT NULL                COMMENT '房源房东ID',
  appointment_time VARCHAR(50) DEFAULT NULL            COMMENT '预约看房时间（字符串）',
  message          VARCHAR(255) DEFAULT NULL           COMMENT '留言',
  status           TINYINT     DEFAULT 0               COMMENT '0待确认 1已确认 2已取消 3已完成',
  create_time      DATETIME    DEFAULT NULL            COMMENT '创建时间',
  update_time      DATETIME    DEFAULT NULL            COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_tenant (tenant_id),
  KEY idx_landlord (landlord_id),
  KEY idx_house (house_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '预约看房表';

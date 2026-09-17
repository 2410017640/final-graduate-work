-- AI 问答记录表（Phase 8）
-- 租客提问 -> 知识库自动回答；答不上则记录为待回答，后续由房东手动回答(P10)

CREATE TABLE IF NOT EXISTS question (
  id            BIGINT   NOT NULL AUTO_INCREMENT COMMENT '问题ID',
  question      TEXT     NOT NULL                COMMENT '租客提问内容',
  answer        TEXT                              COMMENT '回答内容（知识库自动 或 房东手动）',
  house_id      BIGINT   DEFAULT NULL            COMMENT '关联房源ID（可空）',
  landlord_id   BIGINT   DEFAULT NULL            COMMENT '需通知/回答的房东ID',
  asker_id      BIGINT   DEFAULT NULL            COMMENT '提问的租客ID',
  status        TINYINT  DEFAULT 0               COMMENT '0待回答 1已回答',
  answered_by_kb TINYINT DEFAULT 0               COMMENT '是否知识库自动回答 1是 0否',
  create_time   DATETIME DEFAULT NULL            COMMENT '提问时间',
  update_time   DATETIME DEFAULT NULL            COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_landlord (landlord_id),
  KEY idx_asker (asker_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'AI问答记录表';

-- 知识库表（Phase 7）
-- 用于 AI 问答(P8) 与 RAG(P9) 的租房常见问题知识库

CREATE TABLE IF NOT EXISTS knowledge (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '知识ID',
  question    VARCHAR(255) NOT NULL                COMMENT '问题/标题',
  answer      TEXT                                  COMMENT '答案/内容',
  category    VARCHAR(50)  DEFAULT ''              COMMENT '分类（如：押金/合同/退租/费用/合租/宠物）',
  create_time DATETIME     DEFAULT NULL            COMMENT '创建时间',
  update_time DATETIME     DEFAULT NULL            COMMENT '更新时间',
  PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '租房知识库表';

-- 房源表（Phase 3）
CREATE TABLE IF NOT EXISTS house (
  id            BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '房源ID',
  landlord_id   BIGINT       NOT NULL                  COMMENT '发布房东ID(关联 user.id)',
  title         VARCHAR(100) NOT NULL                  COMMENT '标题',
  description   VARCHAR(1000)                          COMMENT '描述',
  address       VARCHAR(255)                           COMMENT '地址',
  area          DECIMAL(10,2)                          COMMENT '面积(㎡)',
  room_count    INT                                   COMMENT '室数',
  hall_count    INT                                   COMMENT '厅数',
  rent          DECIMAL(10,2)                          COMMENT '月租(元)',
  rent_type     TINYINT       DEFAULT 1                COMMENT '租赁方式 1整租 2合租',
  orientation   VARCHAR(20)                            COMMENT '朝向',
  floor         INT                                   COMMENT '楼层',
  total_floor   INT                                   COMMENT '总楼层',
  images        TEXT                                   COMMENT '图片URL，逗号分隔（暂未做文件上传，先存地址）',
  status        TINYINT       DEFAULT 0                COMMENT '状态 0待审核 1已通过(上架) 2已拒绝 3已下架',
  reject_reason VARCHAR(255)                           COMMENT '审核拒绝原因',
  create_time   DATETIME                               COMMENT '创建时间',
  update_time   DATETIME                               COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房源表';

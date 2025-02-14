DROP TABLE IF EXISTS `contact`;
CREATE TABLE `contact` (
                           `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
                           `uid` bigint(20) NOT NULL COMMENT 'uid',
                           `room_id` bigint(20) NOT NULL COMMENT '房间id',
                           `read_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '阅读到的时间',
                           `active_time` datetime(3) DEFAULT NULL COMMENT '会话内消息最后更新的时间(只有普通会话需要维护，全员会话不需要维护)',
                           `last_msg_id` bigint(20) DEFAULT NULL COMMENT '会话最新消息id',
                           `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                           `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
                           PRIMARY KEY (`id`) USING BTREE,
                           UNIQUE KEY `uniq_uid_room_id` (`uid`,`room_id`) USING BTREE,
                           KEY `idx_room_id_read_time` (`room_id`,`read_time`) USING BTREE,
                           KEY `idx_create_time` (`create_time`) USING BTREE,
                           KEY `idx_update_time` (`update_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话列表';
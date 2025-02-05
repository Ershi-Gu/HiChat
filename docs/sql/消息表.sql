DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
`id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
`room_id` bigint(20) NOT NULL COMMENT '会话表id',
`from_uid` bigint(20) NOT NULL COMMENT '消息发送者uid',
`status` int(11) NOT NULL COMMENT '消息状态 0正常 1删除',
`type` int(11) NULL DEFAULT 1 COMMENT '消息类型',
`extra` json DEFAULT NULL COMMENT '扩展信息： 1. content：消息内容 2. img_size：如果是图片消息会有长宽高的一些信息 3. reply_msg_id：回复的 id 4. gap_count：与回复消息之间相差的信息条数 .... 具体分消息类型，查看设计文档',
`create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
`update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
PRIMARY KEY (`id`) USING BTREE,
INDEX `idx_room_id`(`room_id`) USING BTREE,
INDEX `idx_from_uid`(`from_uid`) USING BTREE,
INDEX `idx_create_time`(`create_time`) USING BTREE,
INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息表' ROW_FORMAT = Dynamic;
DROP TABLE IF EXISTS `secure_invoke_record`;
CREATE TABLE `secure_invoke_record` (
`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
`secure_invoke_json` json NOT NULL COMMENT '请求快照参数json',
`status` tinyint(8) NOT NULL COMMENT '状态 1待执行 2已失败',
`next_retry_time` datetime(3) NOT NULL COMMENT '下一次重试的时间',
`retry_times` int(11) NOT NULL COMMENT '已经重试的次数',
`max_retry_times` int(11) NOT NULL COMMENT '最大重试次数',
`fail_reason` text COMMENT '执行失败的堆栈',
`create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
`update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
PRIMARY KEY (`id`) USING BTREE,
KEY `idx_next_retry_time` (`next_retry_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='本地消息表';

-- 模拟数据
INSERT INTO `user` (`id`, `name`, `avatar`, `sex`, `open_id`, `last_opt_time`, `ip_info`, `item_id`, `status`, `create_time`, `update_time`) VALUES (1, '系统消息', 'http://mms1.baidu.com/it/u=1979830414,2984779047&fm=253&app=138&f=JPEG&fmt=auto&q=75?w=500&h=500', NULL, '0', '2023-07-01 11:58:24.605', NULL, NULL, 0, '2023-07-01 11:58:24.605', '2023-07-01 12:02:56.900');
insert INTO `room`(`id`,`type`,`hot_flag`) values (1,1,1);
insert INTO `room_group`(`id`,`room_id`,`name`,`avatar`) values (1,1,'HiChat全员群','https://mallchat.cn/assets/logo-e81cd252.jpeg');
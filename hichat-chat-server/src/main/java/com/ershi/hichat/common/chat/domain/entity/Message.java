package com.ershi.hichat.common.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.ershi.hichat.common.chat.domain.entity.msg.MessageExtra;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 消息表
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2025-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("message")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id（使用自增id，通过id确保消息时序性）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话表id
     */
    @TableField("room_id")
    private Long roomId;

    /**
     * 消息发送者uid
     */
    @TableField("from_uid")
    private Long fromUid;

    /**
     * 消息状态 0正常 1删除
     */
    @TableField("status")
    private Integer status;

    /**
     * 扩展信息： 1. content：消息内容 2. img_size：如果是图片消息会有长宽高的一些信息 3. reply_msg_id：回复的 id 4. gap_count：与回复消息之间相差的信息条数 .... 具体分消息类型，查看设计文档
     */
    @TableField("extra")
    private MessageExtra extra;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;


}

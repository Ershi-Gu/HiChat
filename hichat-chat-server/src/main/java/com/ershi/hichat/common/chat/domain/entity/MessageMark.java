package com.ershi.hichat.common.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 消息标记表
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2025-02-13
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("message_mark")
public class MessageMark implements Serializable {

    private static final long serialVersionUID = -2287133721173708404L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 消息表id
     */
    @TableField("msg_id")
    private Long msgId;

    /**
     * 标记人uid
     */
    @TableField("uid")
    private Long uid;

    /**
     * 标记类型 1点赞 2举报
     */
    @TableField("type")
    private Integer type;

    /**
     * 消息状态 1执行 2取消
     */
    @TableField("status")
    private Integer status;

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

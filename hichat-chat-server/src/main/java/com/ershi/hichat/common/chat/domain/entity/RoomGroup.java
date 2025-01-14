package com.ershi.hichat.common.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 群聊房间表
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2024-12-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("room_group")
public class RoomGroup implements Serializable {

    private static final long serialVersionUID = 2882666910586980082L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 房间id
     */
    @TableField("room_id")
    private Long roomId;

    /**
     * 群名称
     */
    @TableField("name")
    private String name;

    /**
     * 群头像
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 额外信息（根据不同类型房间有不同存储的东西）
     */
    @TableField("ext_json")
    private String extJson;

    /**
     * 逻辑删除(0-正常,1-删除)
     */
    @TableField("delete_status")
    private Integer deleteStatus;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;


}

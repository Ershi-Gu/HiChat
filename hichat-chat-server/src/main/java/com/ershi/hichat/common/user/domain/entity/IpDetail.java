package com.ershi.hichat.common.user.domain.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * ip关联的详细信息，包括属地、运营商等
 * @author Ershi
 * @date 2024/12/12
 */
@Data
public class IpDetail implements Serializable {

    private static final long serialVersionUID = -6947596400646830783L;

    /**
     * IpDetail中保存的最新ip，用于判断是否需要刷新详情
     */
    private String ip;
    /**
     * 国家
     */
    private String country;
    /**
     * 省份地区
     */
    private String region;
    /**
     * 市
     */
    private String city;
    /**
     * 运营商
     */
    private String isp;
}

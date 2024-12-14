package com.ershi.hichat.common.user.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * 用户Ip相关信息
 * @author Ershi
 * @date 2024/12/12
 */
@Data
public class IpInfo implements Serializable {

    private static final long serialVersionUID = -3840620746287089561L;

    /**
     * 注册时的ip
     */
    private String createIp;
    /**
     * 注册时的ip详情
     */
    private IpDetail createIpDetail;
    /**
     * 最新登录的ip
     */
    private String updateIp;
    /**
     * 最新登录的ip详情
     */
    private IpDetail updateIpDetail;


    /**
     * 刷新当前ip信息
     * @param ip
     */
    public void refresh(String ip) {
        updateIp = ip;
        if (createIp == null) {
            createIp = ip;
        }
    }

    /**
     * 判断是否需要重新解析ip属地等详细信息
     * @return {@link String} 需要重新解析返回最新ipv4地址，反之返回null
     */
    public String needRefreshIpDetail() {
        boolean notNeedRefreshIpDetail = Optional.ofNullable(updateIpDetail)
                .map(IpDetail::getIp)
                .filter(ip -> Objects.equals(updateIp, ip))
                .isPresent();
        return notNeedRefreshIpDetail ? null : updateIp;
    }

    /**
     * 更新ipDetail
     * @param ipDetail
     */
    public void refreshIpDetail(IpDetail ipDetail) {
        if (Objects.equals(createIp, ipDetail.getIp())) {
            createIpDetail = ipDetail;
        }
        if (Objects.equals(updateIp, ipDetail.getIp())) {
            updateIpDetail = ipDetail;
        }
    }
}

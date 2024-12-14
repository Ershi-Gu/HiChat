package com.ershi.hichat.common.common.service;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.ershi.hichat.common.common.domain.dto.IpResult;
import com.ershi.hichat.common.common.exception.BusinessErrorEnum;
import com.ershi.hichat.common.common.exception.BusinessException;
import com.ershi.hichat.common.user.domain.entity.IpDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Ip属地详情查询服务类（通用）
 *
 * @author Ershi
 * @date 2024/12/11
 */
@Slf4j
@Service
public class IpQueryService {

    /**
     * 淘宝ip库查询地址
     */
    private static final String IP_FOR_TAOBAO_IP_UTILS = "https://ip.taobao.com/outGetIpInfo?ip=";
    /**
     * 淘宝ip库查询密钥
     */
    private static final String ACCESS_KEY_FOR_TAOBAO_IP_UTILS = "&accessKey=alibaba-inc";

    /**
     * ip2region提供的搜索器，配置于hichat-common-starter#Ip2RegionConfig
     */
    @Autowired
    private Searcher searcher;


    /**
     * 查询ip归属地详细信息（ip2region）
     *
     * @param ip
     * @return {@link IpDetail} 查询成功返回ipDetail，失败返回null
     */
    public IpDetail searchIpFromIp2Region(String ip) {
        try {
            String ipDetailString = searchIpDetailStringFromIp2Region(ip);
            IpDetail ipDetail = parseIpDetail(ipDetailString);
            if (Objects.nonNull(ipDetail)) {
                ipDetail.setIp(ip);
                return ipDetail;
            }
        } catch (Exception e) {
            log.error("ip2region Exception! The reason is: {}", e.getMessage());
        }
        return null;
    }


    /**
     * 查询ip归属地（ip2region）
     *
     * @param ip IPv4地址
     * @return {@link String} 查询成功返回归属地字符串-“中国|0|河南省|郑州市|移动”，若查询不到则返回null！
     * @throws Exception
     */
    private String searchIpDetailStringFromIp2Region(String ip) throws Exception {
        return searcher.search(ip);
    }

    /**
     * 解析ipDetail字符串为IpDetail对象
     *
     * @param ipDetailString
     * @return {@link IpDetail}
     */
    private IpDetail parseIpDetail(String ipDetailString) {
        if (StringUtils.isBlank(ipDetailString)) {
            return null;
        }

        IpDetail ipDetail = new IpDetail();
        String[] parts = ipDetailString.split("\\|");
        if (parts.length == 5) {
            ipDetail.setCountry(parts[0]);  // 国家
            ipDetail.setRegion(parts[2]);   // 省份地区
            ipDetail.setCity(parts[3]);     // 市
            ipDetail.setIsp(parts[4]);      // 运营商
        } else {
            throw new BusinessException(BusinessErrorEnum.IP_ANALYSIS_ERROR);
        }

        return ipDetail;
    }

    /**
     * 查询ip归属地详情（淘宝ip库）
     *
     * @param ip
     * @return {@link IpDetail}
     */
    public IpDetail getIpDetailFromTaobao(String ip) {
        String body = HttpUtil.get(IP_FOR_TAOBAO_IP_UTILS + ip + ACCESS_KEY_FOR_TAOBAO_IP_UTILS);
        try {
            IpResult<IpDetail> result = JSONUtil.toBean(body, new TypeReference<IpResult<IpDetail>>() {
            }, false);
            if (result.isSuccess()) {
                return result.getData();
            }
        } catch (Exception e) {
            log.error("ip.taobao.com Exception! The reason is: {}", e.getMessage());
        }
        return null;
    }
}

package com.ershi.hichat.common.user.service.imp;

import cn.hutool.core.thread.NamedThreadFactory;
import com.ershi.hichat.common.common.handler.GlobalThreadUncaughtExceptionHandler;
import com.ershi.hichat.common.common.service.IpQueryService;
import com.ershi.hichat.common.user.dao.UserDao;
import com.ershi.hichat.common.user.domain.entity.IpDetail;
import com.ershi.hichat.common.user.domain.entity.IpInfo;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.service.IpForUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 用户ip解析相关服务类
 * @author Ershi
 * @date 2024/12/14
 */
@Slf4j
@Service
public class IpForUserServiceImpl implements IpForUserService, DisposableBean {

    /**
     * 从淘宝重试获取ip间隔时间
     */
    public static final int GET_IP_DETAIL_FROM_TAOBAO_INTERVAL_MILLISECOND = 2000;
    /**
     * 从淘宝获取ip重试次数
     */
    public static final int RETRY_TIMES = 3;

    /**
     * ip解析线程池核心线程数
     */
    public static final int IP_ANALYSIS_EXECUTOR_CORE_POOL_SIZE = 1;
    /**
     * ip解析线程池最大线程数
     */
    public static final int IP_ANALYSIS_EXECUTOR_MAXIMUM_POOL_SIZE = 1;
    /**
     *ip解析线程池临时线程存活时间
     */
    public static final long IP_ANALYSIS_EXECUTOR_KEEP_ALIVE_TIME = 0L;
    /**
     *ip解析线程池任务队列最大容量
     */
    public static final int IP_ANALYSIS_EXECUTOR_QUEUE_CAPACITY = 500;

    /**
     * ip2region本地ip属地解析服务
     */
    @Autowired
    private IpQueryService ipQueryService;

    @Autowired
    private UserDao userDao;

    /**
     * ip解析线程池 -> 用于淘宝ip解析库
     */
    private static final ExecutorService IP_ANALYSIS_EXECUTOR =
            new ThreadPoolExecutor(IP_ANALYSIS_EXECUTOR_CORE_POOL_SIZE,
                    IP_ANALYSIS_EXECUTOR_MAXIMUM_POOL_SIZE,
                    IP_ANALYSIS_EXECUTOR_KEEP_ALIVE_TIME,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(IP_ANALYSIS_EXECUTOR_QUEUE_CAPACITY), new NamedThreadFactory("refresh-ipDetail", null, false, GlobalThreadUncaughtExceptionHandler.getInstance()));

    /**
     * 异步更新用户当前登录Ip详情
     * @param uid
     */
    @Override
    public void refreshIpDetailAsync(Long uid) {
        // 获取用户ip地址
        User user = userDao.getById(uid);
        IpInfo ipInfo = user.getIpInfo();
        if (Objects.isNull(ipInfo)) {
            return;
        }

        // 判断ip详情是否需要更新
        String updateIp = ipInfo.needRefreshIpDetail();
        if (StringUtils.isBlank(updateIp)) {
            return;
        }
        // 本地ip2region解析
        IpDetail ipDetail = ipQueryService.searchIpFromIp2Region(updateIp);
        // 判断本地解析是否成功
        if (Objects.isNull(ipDetail)) {
            // 本地解析失败，走淘宝ip库解析流程
            IP_ANALYSIS_EXECUTOR.execute(() -> {
                IpDetail ipDetailFormTaobao = tryGetIpDetailFromTaobao(updateIp);
                if (Objects.nonNull(ipDetailFormTaobao)) {
                    ipInfo.refreshIpDetail(ipDetailFormTaobao);
                    saveUpdateIpDetail2DB(uid, ipInfo);
                }
            });
        } else {
            // 本地解析成功，保存ipDetail
            ipInfo.refreshIpDetail(ipDetail);
            saveUpdateIpDetail2DB(uid, ipInfo);
        }
    }

    /**
     * 尝试从淘宝获取ip属地详情（重试3次）
     *
     * @param ip
     * @return {@link IpDetail}
     */
    public IpDetail tryGetIpDetailFromTaobao(String ip) {
        for (int i = 0; i < RETRY_TIMES; i++) {
            IpDetail ipDetail = ipQueryService.getIpDetailFromTaobao(ip);
            if (Objects.nonNull(ipDetail)) {
                return ipDetail;
            }
            // 等待2秒
            try {
                Thread.sleep(GET_IP_DETAIL_FROM_TAOBAO_INTERVAL_MILLISECOND);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * 保存更新后的ip详情到数据库
     *
     * @param uid
     * @param ipInfo
     */
    private void saveUpdateIpDetail2DB(Long uid, IpInfo ipInfo) {
        User updateIpUser = User.builder().id(uid).ipInfo(ipInfo).build();
        userDao.updateById(updateIpUser);
    }

    /**
     * 线程池优雅停机
     *
     * @throws InterruptedException
     */
    @Override
    public void destroy() throws InterruptedException {
        IP_ANALYSIS_EXECUTOR.shutdown();
        if (!IP_ANALYSIS_EXECUTOR.awaitTermination(30, TimeUnit.SECONDS)) {//最多等30秒，处理不完就拉倒
            if (log.isErrorEnabled()) {
                log.error("Timed out while waiting for executor [{}] to terminate", IP_ANALYSIS_EXECUTOR);
            }
        }
    }
}

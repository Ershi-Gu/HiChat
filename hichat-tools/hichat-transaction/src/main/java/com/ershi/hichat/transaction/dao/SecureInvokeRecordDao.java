package com.ershi.hichat.transaction.dao;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ershi.hichat.transaction.domain.entity.SecureInvokeRecord;
import com.ershi.hichat.transaction.mapper.SecureInvokeRecordMapper;
import com.ershi.hichat.transaction.service.SecureInvokeService;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 本地消息表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2025-02-06
 */
@Component
public class SecureInvokeRecordDao extends ServiceImpl<SecureInvokeRecordMapper, SecureInvokeRecord> {

    /**
     * 查询入库至少两分钟的需要重试的方法记录
     * @return {@link List }<{@link SecureInvokeRecord }>
     */
    public List<SecureInvokeRecord> getWaitRetryRecords() {
        Date now = new Date();
        //查2分钟前的失败数据。避免刚入库的数据被查出来
        DateTime afterTime = DateUtil.offsetMinute(now, -(int) SecureInvokeService.RETRY_INTERVAL_MINUTES);
        return lambdaQuery()
                .eq(SecureInvokeRecord::getStatus, SecureInvokeRecord.STATUS_WAIT)
                .lt(SecureInvokeRecord::getNextRetryTime, new Date())
                .lt(SecureInvokeRecord::getCreateTime, afterTime)
                .list();
    }
}

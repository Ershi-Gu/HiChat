package com.ershi.hichat.common.user.service.imp;

import com.ershi.hichat.common.common.annotation.RedissonLock;
import com.ershi.hichat.common.user.dao.UserBackpackDao;
import com.ershi.hichat.common.user.domain.entity.ItemConfig;
import com.ershi.hichat.common.user.domain.entity.UserBackpack;
import com.ershi.hichat.common.user.domain.enums.IdempotentSourceEnum;
import com.ershi.hichat.common.user.domain.enums.UseStatusEnum;
import com.ershi.hichat.common.user.service.UserBackpackService;
import com.ershi.hichat.common.user.service.cache.ItemCache;
import com.ershi.hichat.common.user.service.validator.ItemValidator;
import com.ershi.hichat.common.user.service.validator.ItemValidatorFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserBackpackServiceImpl implements UserBackpackService {

    @Autowired
    private UserBackpackDao userBackpackDao;

    @Autowired
    private ItemCache itemCache;

    @Autowired
    private ItemValidatorFactory itemValidatorFactory;


    /**
     * 用户获取物品
     *
     * @param uid                  用户uid
     * @param itemId               获取物品id
     * @param idempotentSourceEnum 幂等号来源渠道类型
     * @param businessId           该渠道下的业务号，用于组装幂等号
     */
    @Override
    public void acquireItem(Long uid, Long itemId, IdempotentSourceEnum idempotentSourceEnum, String businessId) {
        // 组装幂等号
        String idempotentId = getIdempotentId(itemId, idempotentSourceEnum, businessId);
        // 获取当前类的代理对象（避免this调用导致aop失效）
        UserBackpackService proxy = (UserBackpackService) AopContext.currentProxy();
        // 发放物品
        proxy.doAcquireItem(uid, itemId, idempotentId);
    }

    /**
     * 执行发放物品
     *
     * @param uid          用户id
     * @param itemId       物品id
     * @param idempotentId 幂等号
     */
    @RedissonLock(key = "#idempotentId", waitTime = 5000)
    public void doAcquireItem(Long uid, Long itemId, String idempotentId) {
        // 通过唯一幂等号进行幂等检查
        UserBackpack userBackpack = userBackpackDao.getByIdp(idempotentId);
        if (Objects.nonNull(userBackpack)) { // 不幂等直接返回即可，无需提示
            return;
        }
        // 业务检验，根据不同的物品类型进行不同的验证
        ItemConfig itemConfig = itemCache.getById(itemId);
        Integer itemType = itemConfig.getType();
        ItemValidator validator = itemValidatorFactory.getValidator(itemType);
        if (!validator.validate(uid, itemId)) {
            // 未通过检查 => 不继续发放
            return;
        }
        //发物品
        UserBackpack insert = UserBackpack.builder()
                .uid(uid)
                .itemId(itemId)
                .status(UseStatusEnum.TO_BE_USED.getStatus())
                .idempotent(idempotentId)
                .build();
        userBackpackDao.save(insert);
    }

    /**
     * 构建幂等号
     *
     * @param itemId
     * @param idempotentSourceEnum
     * @param businessId
     * @return {@link String}
     */
    private String getIdempotentId(Long itemId, IdempotentSourceEnum idempotentSourceEnum, String businessId) {
        return String.format("%d_%d_%s", itemId, idempotentSourceEnum.getType(), businessId);
    }
}

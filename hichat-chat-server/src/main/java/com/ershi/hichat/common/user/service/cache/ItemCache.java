package com.ershi.hichat.common.user.service.cache;

import com.ershi.hichat.common.user.dao.ItemConfigDao;
import com.ershi.hichat.common.user.domain.entity.ItemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.ershi.hichat.common.common.constant.SpringCacheConstant.ITEM_CACHE_NAMES;

/**
 * 物品数据本地缓存
 * @author Ershi
 * @date 2024/12/06
 */
@Component
public class ItemCache {

    @Autowired
    private ItemConfigDao itemConfigDao;

    @Cacheable(cacheNames = ITEM_CACHE_NAMES, key = "'itemByType' + #itemType")
    public List<ItemConfig> getByType(Integer itemType){
        return itemConfigDao.getByType(itemType);
    }
}

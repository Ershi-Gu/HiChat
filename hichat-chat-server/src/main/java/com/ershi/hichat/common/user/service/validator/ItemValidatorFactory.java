package com.ershi.hichat.common.user.service.validator;

import com.ershi.hichat.common.user.domain.enums.ItemTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物品发放业务验证工厂
 * @author Ershi
 * @date 2024/12/08
 */
@Component
public class ItemValidatorFactory {

    /**
     *保存所有验证器实例
     */
    private final Map<String, ItemValidator> validatorMap = new HashMap<>();

    @Autowired
    public ItemValidatorFactory(List<ItemValidator> validators) {
        // 将所有 ItemValidator 实现类注册到 Map 中
        validators.forEach(validator -> validatorMap.put(validator.getClass().getSimpleName(), validator));
    }

    /**
     * 根据物品类型获取对应的业务验证器
     * @param itemType
     * @return {@link ItemValidator}
     */
    public ItemValidator getValidator(Integer itemType) {
        ItemTypeEnum itemTypeEnum = ItemTypeEnum.of(itemType);
        // 根据物品类型获取校验器，未找到时使用默认校验器
        return validatorMap.getOrDefault(itemTypeEnum.getDesc() + " Validator", new DefaultItemValidator());
    }
}

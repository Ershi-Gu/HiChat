package com.ershi.hichat.common.user.service.validator;

import com.ershi.hichat.common.user.domain.enums.ItemTypeEnum;
import org.springframework.stereotype.Component;

/**
 * 默认业务验证器
 * @author Ershi
 * @date 2024/12/08
 */
@Component
public class DefaultItemValidator implements ItemValidator {

    @Override
    public boolean validate(Long uid, Long itemId) {
        // 默认情况下，不需要校验逻辑
        return true;
    }

    @Override
    public ItemTypeEnum getItemType() {
        return null;
    }
}

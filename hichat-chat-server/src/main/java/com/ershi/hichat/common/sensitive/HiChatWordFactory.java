package com.ershi.hichat.common.sensitive;

import com.ershi.hichat.common.sensitive.algorithm.sensitiveWord.SensitiveWordFactory;
import com.ershi.hichat.common.sensitive.dao.SensitiveWordDao;
import com.ershi.hichat.common.sensitive.domain.SensitiveWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * HiChat聊天用敏感词加载工厂
 * @author Ershi
 * @date 2025/02/12
 */
@Component
public class HiChatWordFactory implements SensitiveWordFactory {
    @Autowired
    private SensitiveWordDao sensitiveWordDao;

    @Override
    public List<String> getWordList() {
        return sensitiveWordDao.list()
                .stream()
                .map(SensitiveWord::getWord)
                .collect(Collectors.toList());
    }
}

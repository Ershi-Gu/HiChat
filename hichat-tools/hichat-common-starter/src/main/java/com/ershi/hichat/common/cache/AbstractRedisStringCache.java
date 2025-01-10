package com.ershi.hichat.common.cache;

import cn.hutool.core.collection.CollectionUtil;
import com.ershi.hichat.common.utils.RedisUtils;
import org.springframework.data.util.Pair;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Redis 中 String 类型的批量缓存框架
 * @author Ershi
 * @date 2025/01/10
 */
public abstract class AbstractRedisStringCache<IN, OUT> implements BatchCache<IN, OUT> {

    /**
     * 缓存的输出类型
     */
    private final Class<OUT> outClass;
    protected AbstractRedisStringCache() {
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        // 获取缓存的输出类型，防止泛型擦除导致的数据类型不规范
        this.outClass = (Class<OUT>) genericSuperclass.getActualTypeArguments()[1];
    }

    /**
     * 获取key
     * @param req
     * @return {@link String }
     */
    protected abstract String getKey(IN req);

    /**
     * 获取过期时间
     * @return {@link Long }
     */
    protected abstract Long getExpireSeconds();

    /**
     * 缓存未命中时从数据库加载数据，由子类传入实现
     * @param req
     * @return {@link Map }<{@link IN }, {@link OUT }>
     */
    protected abstract Map<IN, OUT> load(List<IN> req);


    /**
     * 获取单个缓存
     * @param req
     * @return {@link OUT }
     */
    @Override
    public OUT get(IN req) {
        return getBatch(Collections.singletonList(req)).get(req);
    }

    /**
     * 批量获取数据
     *
     * @param req 需要获取的对象集合，比如你要根据uid获取某些信息，传入uid集合即可
     * @return {@link Map }<{@link IN }, {@link OUT }>
     */
    @Override
    public Map<IN, OUT> getBatch(List<IN> req) {
        if (CollectionUtil.isEmpty(req)) { // 防御性编程
            return new HashMap<>();
        }
        // 去重
        req = req.stream().distinct().collect(Collectors.toList());
        // 组装key
        List<String> keys = req.stream().map(this::getKey).collect(Collectors.toList());
        // 从Redis中批量get数据
        List<OUT> dataListFromRedis = RedisUtils.mget(keys, outClass);
        // 计算目标数据量和从Redis中实际获取到的数据量差集 -> 不足的从数据库load
        List<IN> loadReqs = new ArrayList<>();
        for (int i = 0; i < dataListFromRedis.size(); i++) {
            if (Objects.isNull(dataListFromRedis.get(i))) {
                loadReqs.add(req.get(i));
            }
        }
        // 将从数据库load到的数据重新缓存到Reids
        Map<IN, OUT> loadDataFromDb = new HashMap<>();
        if (CollectionUtil.isNotEmpty(loadReqs)) {
            // 从数据库load数据
            loadDataFromDb = load(loadReqs);
            Map<String, OUT> loadMapToRedis = loadDataFromDb.entrySet().stream()
                    .map(a -> Pair.of(getKey(a.getKey()), a.getValue()))// 组装RedisKey，由BASE_KEY + 具体key组成，具体key由不同子类提供不同
                    .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
            // 更新到Redis
            RedisUtils.mset(loadMapToRedis, getExpireSeconds());
        }

        // 组装最后的结果
        Map<IN, OUT> resultMap = new HashMap<>();
        for (int i = 0; i < req.size(); i++) {
            IN in = req.get(i);
            OUT out = Optional.ofNullable(dataListFromRedis.get(i))
                    .orElse(loadDataFromDb.get(in));
            resultMap.put(in, out);
        }
        return resultMap;
    }

    /**
     * 删除单个缓存，收口到批量删除
     * @param req
     */
    @Override
    public void delete(IN req) {
        deleteBatch(Collections.singletonList(req));
    }

    /**
     * 批量删除缓存
     * @param req
     */
    @Override
    public void deleteBatch(List<IN> req) {
        List<String> keys = req.stream().map(this::getKey).collect(Collectors.toList());
        RedisUtils.del(keys);
    }
}

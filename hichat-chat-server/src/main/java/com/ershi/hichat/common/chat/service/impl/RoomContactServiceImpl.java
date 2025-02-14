package com.ershi.hichat.common.chat.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import com.ershi.hichat.common.chat.dao.ContactDao;
import com.ershi.hichat.common.chat.dao.MessageDao;
import com.ershi.hichat.common.chat.domain.dto.RoomBaseInfo;
import com.ershi.hichat.common.chat.domain.entity.*;
import com.ershi.hichat.common.chat.domain.enums.HotFlagEnum;
import com.ershi.hichat.common.chat.domain.enums.RoomTypeEnum;
import com.ershi.hichat.common.chat.domain.vo.request.contact.ChatContactPageReq;
import com.ershi.hichat.common.chat.domain.vo.response.contact.ChatContactResp;
import com.ershi.hichat.common.chat.service.RoomContactService;
import com.ershi.hichat.common.chat.service.RoomFriendService;
import com.ershi.hichat.common.chat.service.adapter.RoomContactAdapter;
import com.ershi.hichat.common.chat.service.cache.HotRoomCache;
import com.ershi.hichat.common.chat.service.cache.RoomCache;
import com.ershi.hichat.common.chat.service.cache.RoomFriendCache;
import com.ershi.hichat.common.chat.service.cache.RoomGroupCache;
import com.ershi.hichat.common.common.utils.AssertUtil;
import com.ershi.hichat.common.domain.vo.response.CursorPageBaseResp;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.service.cache.UserInfoCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomContactServiceImpl implements RoomContactService {

    @Autowired
    private HotRoomCache hotRoomCache;

    @Autowired
    private RoomCache roomCache;

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private RoomGroupCache roomGroupCache;

    @Autowired
    private RoomFriendCache roomFriendCache;

    @Autowired
    private UserInfoCache userInfoCache;

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private RoomFriendService roomFriendService;

    /**
     * 获取会话房间详细信息
     *
     * @param uid
     * @param roomId
     * @return {@link ChatContactResp }
     */
    @Override
    public ChatContactResp getContactDetail(Long uid, long roomId) {
        Room room = roomCache.get(roomId);
        AssertUtil.isNotEmpty(room, "房间不存在");
        return buildContactResp(uid, Collections.singletonList(roomId)).get(0);
    }

    @Override
    public ChatContactResp getContactDetailByFriend(Long uid, Long friendUid) {
        RoomFriend friendRoom = roomFriendService.getFriendRoom(uid, friendUid);
        AssertUtil.isNotEmpty(friendRoom, "不是您的好友");
        return buildContactResp(uid, Collections.singletonList(friendRoom.getRoomId())).get(0);
    }

    /**
     * 聚合获取用户会话列表详情
     *
     * @param chatContactPageReq
     * @param uid
     * @return {@link CursorPageBaseResp }<{@link ChatContactResp }>
     */
    @Override
    public CursorPageBaseResp<ChatContactResp> getContactPage(ChatContactPageReq chatContactPageReq, Long uid) {
        CursorPageBaseResp<Long> resultRoomIdPage;
        // 登录态
        if (Objects.nonNull(uid)) {
            resultRoomIdPage = getContactRoomIdPageOnLogin(chatContactPageReq, uid);
        }
        // 用户未登录只能看全员群
        else {
            resultRoomIdPage = getContactRoomIdPageNoLogin(chatContactPageReq);
        }
        // 如果一个会话都没有就返回空对象
        if (CollectionUtil.isEmpty(resultRoomIdPage.getList())) {
            return CursorPageBaseResp.empty();
        }
        List<ChatContactResp> result = buildContactResp(uid, resultRoomIdPage.getList());
        return CursorPageBaseResp.init(resultRoomIdPage, result);
    }

    /**
     * 聚合获取会话列表-登录状态
     *
     * @param chatContactPageReq
     * @param uid
     * @return {@link CursorPageBaseResp }<{@link Long }>
     */
    private CursorPageBaseResp<Long> getContactRoomIdPageOnLogin(ChatContactPageReq chatContactPageReq, Long uid) {
        // 获取精确聚合所要用到的游标
        /*
         * 具体设计方案查看：https://www.yuque.com/yuqueyonghu8cmbhk/zhpu5k/yyzif5dp6u8cn6eu#Z2cQH
         * 热点群聊在Redis中通过最后消息时间从小到大排序，因此游标查询倒叙即可查询到最新的会话，因此hotRecent最为游标从旧往新查询
         * 当查询的是第一页时，游标为空，hotRecent也为空
         */
        Double hotRecent = getCursorOrNull(chatContactPageReq.getCursor());
        Double hotOld;
        // 用户基础会话查询 -> 查询每个用户自己的收件箱，获取用户在每个房间最新的消息收件情况，按照消息时间排序，最新的最前
        CursorPageBaseResp<Contact> userContactPage = contactDao.getContactPage(chatContactPageReq, uid);
        hotOld = getCursorOrNull(userContactPage.getCursor());
        // 当游标查询到最后一页时，hotOld为空，这样才能查到以热点会话最为最后位的会话
        if (userContactPage.getIsLast()) {
            hotOld = null;
        }
        // 组装用户收件箱的会话房间id信息
        List<Long> baseRoomIds = userContactPage.getList().stream().map(Contact::getRoomId).collect(Collectors.toList());
        // 获取热点房间
        Set<ZSetOperations.TypedTuple<String>> typedTuples = hotRoomCache.getRoomRange(hotOld, hotRecent);
        List<Long> hotRoomIds = typedTuples.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .filter(Objects::nonNull)
                .map(Long::parseLong)
                .collect(Collectors.toList());
        // 聚合热点群聊和基础会话的会话房间id信息-无序，需要在后面的组装根据active_time排序
        baseRoomIds.addAll(hotRoomIds);
        // 基础会话和热门房间合并
        return CursorPageBaseResp.init(userContactPage, baseRoomIds);
    }

    /**
     * 获取用户会话列表-未登录，只能查看全员群
     *
     * @param chatContactPageReq
     * @return {@link CursorPageBaseResp }<{@link Long }>
     */
    private CursorPageBaseResp<Long> getContactRoomIdPageNoLogin(ChatContactPageReq chatContactPageReq) {
        // 获取所有热点群聊排序
        CursorPageBaseResp<Pair<Long, Double>> roomCursorPage = hotRoomCache.getRoomCursorPage(chatContactPageReq);
        List<Long> roomIds = roomCursorPage.getList().stream().map(Pair::getKey).collect(Collectors.toList());
        // 从中筛选出全员群房间id
        List<Long> allRoomIds = getAllRoom(roomIds);
        // 最终结果只返回全员群的id，但是游标还是返回所有热点群的，下一次查询带着热点群的游标继续过滤全员群即可
        return CursorPageBaseResp.init(roomCursorPage, allRoomIds);
    }

    /**
     * 将字符串游标转换为Double类型
     *
     * @param cursor
     * @return {@link Double } or null
     */
    private Double getCursorOrNull(String cursor) {
        return Optional.ofNullable(cursor).map(Double::parseDouble).orElse(null);
    }

    /**
     * 从群聊中筛选出全员群聊
     *
     * @param roomIds
     * @return {@link List }<{@link Long }>
     */
    private List<Long> getAllRoom(List<Long> roomIds) {
        // 获取所有热点群聊信息
        Map<Long, Room> roomMap = roomCache.getBatch(roomIds);
        return roomMap.values().stream()
                .filter(room -> Objects.equals(room.getHotFlag(), HotFlagEnum.ALL.getType()))
                .map(Room::getId)
                .collect(Collectors.toList());
    }

    /**
     * 组装会话信息(房间名称、头像、未读数等等)
     *
     * @param uid
     * @param roomIdList
     * @return {@link List }<{@link ChatContactResp }>
     */
    private List<ChatContactResp> buildContactResp(Long uid, List<Long> roomIdList) {
        // 获取房间基本信息
        Map<Long, RoomBaseInfo> roomBaseInfoMap = getRoomBaseInfoMap(roomIdList, uid);
        // 查询会话房间最后一条消息用于显示在会话列表上
        List<Long> lastMsgIds = roomBaseInfoMap.values().stream().map(RoomBaseInfo::getLastMsgId).collect(Collectors.toList());
        List<Message> lastMessages = CollectionUtil.isEmpty(lastMsgIds) ? new ArrayList<>() : messageDao.listByIds(lastMsgIds);
        // 获取最后一条消息发送者信息
        Map<Long, User> lastMsgSendUserInfoMap = userInfoCache.getBatch(lastMessages.stream().map(Message::getFromUid).collect(Collectors.toList()));
        // 获取用户在各个会话的未读数
        Map<Long, Integer> unReadCountMap = getUnReadMsgCountMap(uid, roomIdList);
        // 组装返回
        return RoomContactAdapter.buildChatContactResp(roomBaseInfoMap, lastMessages, lastMsgSendUserInfoMap, unReadCountMap);
    }

    /**
     * 获取房间基本信息
     *
     * @param roomIdList
     * @param uid
     * @return {@link Map }<{@link Long }, {@link RoomBaseInfo }>
     */
    private Map<Long, RoomBaseInfo> getRoomBaseInfoMap(List<Long> roomIdList, Long uid) {
        Map<Long, Room> roomMap = roomCache.getBatch(roomIdList);
        // 将需要查询的房间根据单聊和群聊类型分组
        Map<Integer, List<Long>> groupRoomIdByRoomTypeMap = roomMap.values().stream()
                .collect(Collectors.groupingBy(Room::getType, Collectors.mapping(Room::getId, Collectors.toList())));
        // 获取群聊详细房间信息[groupId-roomGroup]
        List<Long> groupRoomIds = groupRoomIdByRoomTypeMap.get(RoomTypeEnum.GROUP.getType());
        Map<Long, RoomGroup> groupRoomMap = roomGroupCache.getBatch(groupRoomIds);
        // 获取好友房间信息
        List<Long> friendRoomIds = groupRoomIdByRoomTypeMap.get(RoomTypeEnum.FRIEND.getType());
        Map<Long, User> friendRoomMap = getFriendRoomInfoMap(friendRoomIds, uid);
        // 按房间最新活跃时间组装会话房间详情返回
        return RoomContactAdapter.buildRoomBaseInfo(roomMap, groupRoomMap, friendRoomMap);
    }

    /**
     * 获取单聊房间基本信息
     *
     * @param friendRoomIds
     * @param uid
     * @return {@link Map }<{@link Long }, {@link User }> roomId-FriendUserInfo
     */
    private Map<Long, User> getFriendRoomInfoMap(List<Long> friendRoomIds, Long uid) {
        if (CollectionUtil.isEmpty(friendRoomIds)) {
            return new HashMap<>();
        }
        // 获取单聊房间信息
        Map<Long, RoomFriend> roomFriendMap = roomFriendCache.getBatch(friendRoomIds);
        // 从单聊房间中获取好友的uid
        Set<Long> friendUidSet = RoomContactAdapter.getFriendUidSet(roomFriendMap.values(), uid);
        // 查询好友详情-单聊房间头像、名称显示为对方的信息
        Map<Long, User> userBatch = userInfoCache.getBatch(new ArrayList<>(friendUidSet));
        // 组装返回
        return roomFriendMap.values()
                .stream()
                .collect(Collectors.toMap(RoomFriend::getRoomId, roomFriend -> {
                    Long friendUid = RoomContactAdapter.getFriendUid(roomFriend, uid);
                    return userBatch.get(friendUid);
                }));
    }

    /**
     * 获取用户在指定房间的未读消息数量
     *
     * @param uid
     * @param roomIdList
     * @return {@link Map }<{@link Long }, {@link Integer }>
     */
    private Map<Long, Integer> getUnReadMsgCountMap(Long uid, List<Long> roomIdList) {
        if (Objects.isNull(uid)) {
            return new HashMap<>();
        }
        // 获取用户在各个房间的收件箱会话信息
        List<Contact> contacts = contactDao.getByRoomIds(roomIdList, uid);
        // 根据用户收件箱最后阅读到的时间查询消息未读数
        return contacts.parallelStream()
                .map(contact -> Pair.of(contact.getRoomId(), messageDao.getUnReadCount(contact.getRoomId(), contact.getReadTime())))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }
}

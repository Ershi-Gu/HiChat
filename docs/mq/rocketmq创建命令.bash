# 启动 NameServer
docker run -d --name rmqnamesrv -p 9876:9876 --network hichat[容器网络名] rocketmq[镜像名] sh mqnamesrv

# 启动 Broker
docker run -d \
--name rmqbroker \
--network hichat \
-p 10911:10911 \
-p 10909:10909 \
-e "NAMESRV_ADDR=rmqnamesrv:9876" \
-v /root/rocketmq/broker/logs:/home/rocketmq/logs \
-v /root/rocketmq/broker/store:/home/rocketmq/store \
-v /root/rocketmq/broker.conf:/home/rocketmq/rocketmq-5.3.1/conf/broker.conf \  # 注意这里需要写一个conf文件指定broker的ip，否则后续无法发送消息！
rocketmq \
sh mqbroker -n rmqnamesrv:9876 \
-c /home/rocketmq/rocketmq-5.3.1/conf/broker.conf # 指定使用自定义conf文件启动
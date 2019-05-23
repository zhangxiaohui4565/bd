## 1、Controller的选举

KafkaController -> ZookeeperLeaderElector (向/controller/注册节点)

## 2、Controller的初始化

leader的初始化： KafkaController.onControllerFailover (皇帝登基：修改年号，注册listener，完成TODO任务)

follower的初始化：KafkaController.onControllerResignation (从领导位置退下来，取消相关职能)

## 3、分区状态机：PartitionStateMachine


## 4、副本状态机：ReplicaStateMachine


## 5、partition leader的选举：PartitionLeaderSelector

  > 策略一：NoOpLeaderSelector （默认: 啥也不做)
  > 策略二：ReassignedPartitionLeaderSelector (分区重分配)
  > 策略三：PreferredReplicaPartitionLeaderSelector (自动/手动触发)
  > 策略四：OfflinePartitionLeaderSelector   (分区上线时)
  > 策略五：ControlledShutdownLeaderSelector （broker下线时)

## 6、ControllerContext：缓存ZK中的元数据信息


## 7、监听器：

 注册**Handler来监听不同的zk节点，如果zk节点发生变化，回调handleChildChange来处理(向eventManager发消息)，

 eventManager中启动一个线程来处理消息，消息中自带process方法

## 8、集群的负载均衡：partition leader分散开，落到不同的broker


## 9、Topic的删除流程


## 10、通讯模块

  ControllerChannelManager：controller与普通broker之间通讯



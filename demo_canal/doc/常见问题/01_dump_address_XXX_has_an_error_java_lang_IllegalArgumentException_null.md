 报错信息
 --------
[destination = example , address = /127.0.0.1:3306 , EventParser] ERROR c.a.otter.canal.parse.inbound.mysql.MysqlEventParser - dump address /127.0.0.1:3306 has an error, retrying. caused by
    java.lang.IllegalArgumentException: null
    at java.util.concurrent.ThreadPoolExecutor.(ThreadPoolExecutor.java:1314) ~[na:1.8.0_144]
    at java.util.concurrent.ThreadPoolExecutor.(ThreadPoolExecutor.java:1237) ~[na:1.8.0_144]
    at java.util.concurrent.Executors.newFixedThreadPool(Executors.java:151) ~[na:1.8.0_144]
    at com.alibaba.otter.canal.parse.inbound.mysql.MysqlMultiStageCoprocessor.start(MysqlMultiStageCoprocessor.java:84) ~[canal.parse-1.0.26-SNAPSHOT.jar:na]
    at com.alibaba.otter.canal.parse.inbound.AbstractEventParser$3.run(AbstractEventParser.java:230) ~[canal.parse-1.0.26-SNAPSHOT.jar:na]
    at java.lang.Thread.run(Thread.java:748) [na:1.8.0_144]
    2018-08-16 12:21:45.933 [destination = example , address = /127.0.0.1:3306 , EventParser] ERROR com.alibaba.otter.canal.common.alarm.LogAlarmHandler - destination:example[java.lang.IllegalArgumentException
    at java.util.concurrent.ThreadPoolExecutor.(ThreadPoolExecutor.java:1314)
    at java.util.concurrent.ThreadPoolExecutor.(ThreadPoolExecutor.java:1237)
    at java.util.concurrent.Executors.newFixedThreadPool(Executors.java:151)
    at com.alibaba.otter.canal.parse.inbound.mysql.MysqlMultiStageCoprocessor.start(MysqlMultiStageCoprocessor.java:84)
    at com.alibaba.otter.canal.parse.inbound.AbstractEventParser$3.run(AbstractEventParser.java:230)
    at java.lang.Thread.run(Thread.java:748)
    
解决方案
--------
修改conf/canal.properties，解注：canal.instance.parser.parallelThreadSize （默认被注释掉了,在单核cpu的环境下需要开启）   


# parallel parser config
canal.instance.parser.parallel = true
## concurrent thread number, default 60% available processors, suggest not to exceed Runtime.getRuntime().availableProcessors()
canal.instance.parser.parallelThreadSize = 16
## disruptor ringbuffer size, must be power of 2
canal.instance.parser.parallelBufferSize = 256
 
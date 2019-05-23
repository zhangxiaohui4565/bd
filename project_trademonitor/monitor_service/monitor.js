//功能：服务端程序，主动推送统计指标到监控大屏

// 引入外部模块
var express = require('express');
var kafka = require('kafka-node');

var app = express();
var server = require('http').Server(app);
var io = require('socket.io')(server);

// 初始化系统配置
var conf = require(__dirname + "/conf.js")();
console.log("kafka conf:" + conf.kafka["zk.quorum"]);

// 在指定端口启动服务
var port = conf.node["listen.port"];
server.listen(port, function () {
    console.log("Running on http://localhost:" + port)
});

// 处理静态资源的请求(html/js/css/image)
app.use(express.static(__dirname + '/public'));


// 处理UA连接事件
io.on('connection', function (socket) {
    console.log('a user connected');

    socket.on('disconnect', function () {
        console.log('user disconnected');
    });
});

// 创建kafka consumer
var client = new kafka.Client(conf.kafka["zk.quorum"]);

var options = {
    groupId: 'monitor_service',
    //fromOffset: 'latest',
    autoCommit: true,
    autoCommitIntervalMs: 1000
};

var consumer = new kafka.HighLevelConsumer(client, [{topic: conf.kafka["topic.metric"]}], options);

// 从kafka消费消息并推送至浏览器
consumer.on('message', function (message) {
    const  val = message.value;
    if(val !== undefined && val !== null && val != ""){
        console.log("指标：" + val);
        io.emit("metric", val);
    }
});

// 处理kafka异常
consumer.on('error', function (message) {
    console.log('kafka错误:' + message);
});


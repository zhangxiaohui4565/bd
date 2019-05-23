//功能：读取并解析配置文件，获取kafka／mysql等连接信息

var fs = require('fs');
var stripJsonComments = require('strip-json-comments');

function loadJSONFile(file) {
    var json = fs.readFileSync(file).toString();
    return JSON.parse(stripJsonComments(json));
}

function Conf() {
    return loadJSONFile(__dirname + '/./conf/service.json');
}

module.exports = Conf;


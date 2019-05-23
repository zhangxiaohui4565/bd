//** 功能：模拟用户注册、下单（直接往MySQL中insert记录）**

var MySql = require(__dirname + "/mysql.js");
var mysql = new MySql();
var Util = require(__dirname + "/util.js");
var util = new Util();
var conf = require(__dirname + "/conf.js")();

//生成一笔订单
function buildOrder() {
    var sql = 'INSERT INTO wuchen.trade(user_id,order_id,p_id,buy_count,total_price,order_status,order_time,create_time,update_time) VALUES(?,?,?,?,?,?,?,?,?)';

    var dt = util.current_time(new Date(), "yyyy-MM-dd hh:mm:ss");
    var userId = util.randomNum(1, 20);
    var orderId = util.randomNum(1, 10000);
    var pId = util.randomNum(1, 4);
    var buyCount = util.randomNum(1, 10);
    var price = util.randomNum(1000, 100000);
    var params = [userId, orderId, pId, buyCount, price, 'payed', dt, dt, dt];

    mysql.execSQL(sql, params, dt + " => 用户购买[ " + pId + " ]" + buyCount + "件,共" + +price + "元");
}

//生成一个用户
function buildUser() {
    var sql = 'insert into wuchen.`user`(user_name,mobile_num,register_time,city,create_time,update_time) values(?,?,?,?,?,?)';

    var dt = util.current_time(new Date(), "yyyy-MM-dd hh:mm:ss");
    var cityId = util.randomNum(1, 6);
    var cityList = ["北京", "上海", "深圳", "广州", "杭州", "武汉"];
    var city = cityList[cityId - 1];

    var params = ["wuchen" + cityId, "110", dt, city, dt, dt];

    mysql.execSQL(sql, params, dt + " => 来自[ " + city + " ]的用户,注册成功");
}

//每3秒注册一个用户
var myInterval = setInterval(buildUser, 1000 * 3);


//每5秒生成一个订单
var myInterval2 = setInterval(buildOrder, 1000 * 5);


//一段时间后关闭模拟程序(防止数据过多）
setTimeout(function () {
    clearTimeout(myInterval);
    clearTimeout(myInterval2);
    console.info("after running " + conf.timeout + "(分钟),mock_www stopped.");
}, 1000 * 60 * conf.timeout);



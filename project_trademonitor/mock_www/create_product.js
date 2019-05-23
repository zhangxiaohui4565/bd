//** 功能：模拟产品上架 **

var MySql = require(__dirname + "/mysql.js");
var mysql = new MySql();
var Util = require(__dirname + "/util.js");
var util = new Util();
var conf = require(__dirname + "/conf.js")();

// 创建产品
(function () {
    var pl = [{id: 1, pName: 'iPhone'}, {id: 2, pName: '小米'}, {id: 3, pName: 'OPPO'}, {id: 4, pName: '华为'}];

    var sql = 'INSERT INTO wuchen.product(id,p_name,p_category,create_time,update_time) VALUES(?,?,?,?,?)';

    var dt = util.current_time(new Date(), "yyyy-MM-dd hh:mm:ss");

    for (var i = 0; i < 4; i++) {
        var params = [pl[i].id, pl[i].pName, "智能手机", dt, dt];

        mysql.execSQL(sql, params, dt + " => 产品上架[ " + pl[i].pName + " ]");
    }
})();





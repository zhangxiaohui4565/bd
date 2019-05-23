//功能：连接MySQL及执行sql语句

var mysql = require('mysql');
var conf = require(__dirname + "/conf.js")();
var connStr = conf.mysql;

console.log("mysql info => " + connStr.host + ":" + connStr.port + "/" + connStr.database);

//执行SQL
module.exports = function () {
    this.execSQL = function (sql, params, succMsg) {
        var connection = mysql.createConnection({
            host: connStr.host,
            user: connStr.user,
            password: connStr.password,
            database: connStr.database
        });
        connection.connect();

        connection.query(sql, params, function (err, result) {
            if (err) {
                console.log('[INSERT ERROR] - ', err.message);
                return;
            }
            if (succMsg != "") {
                console.log(succMsg);
            }
        });

        connection.end();
    }
};


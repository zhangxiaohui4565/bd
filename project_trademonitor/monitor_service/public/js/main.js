// 功能:接受服务端的数据并绘制图表

$(document).ready(function () {
    Highcharts.setOptions({
        global: {
            useUTC: false
        },
        lang: {
            thousandsSep: ','
        }
    });

    var lineChart1 = new Highcharts.Chart(lineOption_1);
    var lineChart2 = new Highcharts.Chart(lineOption_2);

    var mapChart = echarts.init(document.getElementById('mapChart'));
    var pieChart = echarts.init(document.getElementById('pieChart'));
    var barChart = echarts.init(document.getElementById('barChart'));

    mapChart.setOption(mapOption, true);
    pieChart.setOption(pieOption, true);
    barChart.setOption(barOption, true);

    var socket = io();
    socket.on('metric', function (row) {
        console.log("metric:" + row);
        var json = JSON.parse(row);
        var metricType = json["metric"];
        switch (metricType) {
            case "MAccTradePrice":
                redrawLine1(json);
                redrawCard(json);
                break;
            case "MIncTradePrice":
                redrawLine2(json);
                break;
            case "MAccRegisterUserCntPerCity":
                redrawMap(json);
                break;
            case "MAccTradeCntPerUserType":
                redrawPie(json);
                break;
            case "MAccTradePricePerProduct":
                redrawBar(json);
                break;
            default:
                console.warn(" invalid metric:" + metricType)
        }
    });

    //每秒钟更新一次页面时间
    setInterval(function () {
        $("#timing").text(moment().format("YYYY-MM-DD HH:mm:ss"));
    }, 1000);

    function getTimeStamp(dtStr) {
        return new Date(dtStr).getTime();
    }

    function redrawLine1(row) {
        var dt = getTimeStamp(row["dt"]);
        var v = [dt, row["value"]]; //交易金额
        lineChart1.series[0].addPoint(v, false, true);
        lineChart1.redraw();
    }


    function toThousands(num) {
        var result = [], counter = 0;
        num = (num || 0).toString().split('');
        for (var i = num.length - 1; i >= 0; i--) {
            counter++;
            result.unshift(num[i]);
            if (!(counter % 3) && i != 0) {
                result.unshift(',');
            }
        }
        return result.join('');
    }

    function redrawCard(row) {
        var dt = getTimeStamp(row["dt"]);
        var v = row["value"]; //交易金额

        $("#amt").text(toThousands(parseInt(v)));
    }

    function redrawLine2(row) {
        var dt = getTimeStamp(row["dt"]);
        var v = [dt, row["value"]]; //交易金额
        lineChart2.series[0].addPoint(v, false, true);
        lineChart2.redraw();
    }

    function redrawMap(row) {
        var v = {
            "name": row["key"], //城市名称
            "value": geoCoordMap[row["key"]].concat(row["value"] * 10) //注册人数，放大100倍
        };

        mapOption.series[0].row = updateList(mapOption.series[0].data, v);
        mapChart.setOption(mapOption);
    }

    function redrawPie(row) {
        var v = {
            "name": row["key"], //用户类型
            "value": row["value"] //交易人数
        };
        pieOption.series[0].row = updateList(pieOption.series[0].data, v);
        pieChart.setOption(pieOption);
    }

    function redrawBar(row) {
        var seriesMap = {"iPhone": 0, "小米": 1, "OPPO": 2, "华为": 3};
        var seriesId = seriesMap[row["key"]]; //产品名称

        var d = barOption.series[0].data;
        d[seriesId] = row["value"]; //交易额

        //var v = {
        //    "name": row["key"], //产品名称
        //    "value": row["value"]
        //};

        barOption.series[0].data = d;
        barChart.setOption(barOption);
    }

    function updateList(list, element) {
        var bList = list;
        for (var i = 0; i < list.length; i++) {
            if (list[i].name == element.name) {
                list[i] = element;
                return bList;
            }
        }
    }
});
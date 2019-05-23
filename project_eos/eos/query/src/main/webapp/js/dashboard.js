require.config({
    paths: {
        echarts: 'js/echarts/dist'
    }
});

require(
    [
        'echarts',
        'echarts/chart/bar',
        'echarts/chart/line'
    ],
    function (ec) {
        $.ajax({
            url: ROUTES.ALL_SERVICE_LOG_SIZE,
            type: 'GET',
            error: function (m) {
                $('#logSizeBar').html('<p><error>查询失败：' + m + '</error></p>');
            },
            success: function (response) {
                var serviceLogSizeMap = response.payload;
                var serviceIds = [];
                var logSizes = [];
                for (var serviceId in serviceLogSizeMap) {
                    serviceIds.push(serviceId);
                    var logSize = serviceLogSizeMap[serviceId];
                    logSizes.push(logSize);
                }
                var logSizeBar = ec.init(document.getElementById('logSizeBar'));

                var option = {
                    title : {
                        text: ''
                    },
                    tooltip : {
                        trigger: 'axis'
                    },
                    legend: {
                        data:['日志量']
                    },
                    toolbox: {
                        show : false
                    },
                    calculable : true,
                    xAxis : [
                        {
                            type : 'value',
                            boundaryGap : [0, 0.01]
                        }
                    ],
                    yAxis : [
                        {
                            type : 'category',
                            data : serviceIds
                        }
                    ],
                    series : [
                        {
                            name: '日志量',
                            type:'bar',
                            data: logSizes
                        }
                    ]
                };

                logSizeBar.setOption(option);
            }
        });

        $.ajax({
            url: ROUTES.SERVICE_LOG_SIZE,
            type: 'GET',
            error: function (m) {
                $('#logSizeLine').html('<p><error>查询失败：' + m + '</error></p>');
            },
            success: function (response) {
                var serviceLogSizeList = response.payload.sort(compare('date'));
                var serviceIds = [];
                var dates = [];
                var dateTimestamps = [];
                var serviceLogSizeData = [];
                for (var i = 0; i < serviceLogSizeList.length; ++i) {
                    var serviceLogSizeMap = serviceLogSizeList[i];
                    var serviceId = serviceLogSizeMap['serviceId'];
                    if (0 > $.inArray(serviceId, serviceIds)) {
                        serviceIds.push(serviceId);
                    }

                    var dateTs = serviceLogSizeMap['date'];
                    var date = new Date(dateTs).format('yyyy/MM/dd');
                    if (0 > $.inArray(date, dates)) {
                        dates.push(date);
                    }
                    if (0 > $.inArray(dateTs, dateTimestamps)) {
                        dateTimestamps.push(dateTs);
                    }
                }
                for (var i = 0; i < dateTimestamps.length; ++i) {
                    var dateTs = dateTimestamps[i];
                    for (var k = 0; k < serviceIds.length; ++k) {
                        var serviceId = serviceIds[k];
                        var logSize = getLogSizeOn(serviceLogSizeList, serviceId, dateTs);

                        var element = getLogSizeElement(serviceLogSizeData, serviceId);
                        if (null == element) {
                            serviceLogSizeData.push({
                                name: serviceId,
                                type: 'line',
                                itemStyle: {normal: {areaStyle: {type: 'default'}}},
                                data: [logSize]
                            })
                        } else {
                            element['data'].push(logSize);
                        }
                    }
                }
                var logSizeLine = ec.init(document.getElementById('logSizeLine'));

                var option = {
                    title : {
                        text: '',
                    },
                    tooltip : {
                        trigger: 'axis'
                    },
                    legend: {
                        data: serviceIds
                    },
                    toolbox: {
                        show : false,
                    },
                    calculable : true,
                    xAxis : [
                        {
                            type : 'category',
                            boundaryGap : false,
                            data : dates
                        }
                    ],
                    yAxis : [
                        {
                            type : 'value'
                        }
                    ],
                    series : serviceLogSizeData
                };

                logSizeLine.setOption(option);
            }
        });
    }

);

function compare(property) {
    return function(a, b){
        var d1 = a[property];
        var d2 = b[property];
        return d1 - d2;
    }
}

function getLogSizeElement(serviceLogSizeData, serviceId) {
    for (var i = 0; i < serviceLogSizeData.length; ++i) {
        if (serviceId == serviceLogSizeData[i]['name']) {
            return serviceLogSizeData[i];
        }
    }
    return null;
}

function getLogSizeOn(serviceLogSizeList, serviceId, dateTs) {
    for (var j = 0; j < serviceLogSizeList.length; ++j) {
        var serviceLogSizeMap = serviceLogSizeList[j];
        if (dateTs == serviceLogSizeMap['date'] && serviceId == serviceLogSizeMap['serviceId']) {
            return serviceLogSizeMap['size'];
        }
    }
    return 0;
}

layui.use(['element', 'layer'], function(){
    var element = layui.element;
    var layer = layui.layer;
});
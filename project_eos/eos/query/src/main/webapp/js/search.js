var currentPage = 0;
var totalPage = 0;
var AUTO_REFRESH_SECS = 3;
var AUTO_REFRESH_MAX_SECS = 10 * 60;
var AUTO_REFRESH_MAX_MILLS = AUTO_REFRESH_MAX_SECS * 1000;
var AUTO_REFRESH_INTERVAL = AUTO_REFRESH_SECS * 1000;
var autoRefreshFlag = false;
var autoRefreshautoRefreshTimer;

(function ($) {
    $(function () {
        initForm();
        initPage();
        loadServiceIds();
    })
}(jQuery));

function initPage() {
    $("#search").click(function (e) {
        e.preventDefault();
        currentPage = 0;
        $('#logs').html('');
        searchLogs();
    });

    $('#prePage').click(function (e) {
        if ($('#prePage').hasClass('page-disable')) {
            return;
        }
        --currentPage;
        searchLogs();
    });

    $('#nextPage').click(function (e) {
        if ($('#nextPage').hasClass('page-disable')) {
            return;
        }
        ++currentPage;
        searchLogs();
    });

    $('#firstPage').click(function (e) {
        if ($('#firstPage').hasClass('page-disable')) {
            return;
        }
        currentPage = 0;
        searchLogs();
    });

    $('#lastPage').click(function (e) {
        if ($('#lastPage').hasClass('page-disable')) {
            return;
        }
        currentPage = totalPage;
        searchLogs();
    });
}

function getTimestamp(dateStr) {
    var date = new Date(dateStr);
    return date.getTime();
}

function loadServiceIds() {
    var startTime = getTimestamp($('#startTime').val());
    var endTime = getTimestamp($('#endTime').val());

    $.ajax({
        url: ROUTES.ALL_SERVICE_IDS,
        type: 'GET',
        error: function (m) {
            $('#logs').html('<p><error>查询失败：' + m + '</error></p>');
        },
        success: function (response) {
            var serviceIds = response.payload;
            for (var i = 0; i < serviceIds.length; ++i) {
                var serviceId = serviceIds[i];
                var option = '<option value="' + serviceId + '"';
                if (i == 0) {
                    option += ' selected="selected"'
                }
                option += '>' + serviceId + '</option>';
                $("#serviceIds").append(option);
            }

            layui.form.render('select', 'searchFrom');
            // search when loading page
            searchLogs();
        }
    });
}

function searchLogs() {
    var pageSize = 50;
    var startTime = getTimestamp($('#startTime').val()) || getInitStartTime();
    var endTime = getTimestamp($('#endTime').val()) || getInitEndTime();

    if (endTime < startTime) {
        alert('起始时间必须小于结束时间');
        return;
    }
    if ((endTime - startTime) > 86400 * 1000 * 3) {
        alert('日志搜索范围必须在3天内');
        return;
    }

    if (!!!autoRefreshFlag) {
        // TODO: loading gif
        $("#logs").prepend('日志搜索中...');
    }

    $.ajax({
        url: ROUTES.QUERY,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        data: JSON.stringify({
            serviceId: $('#serviceIds').val(),
            queryString: $('#queryString').val(),
            level: $('#logLevel').val(),
            startTime: startTime,
            endTime: endTime,
            page: currentPage,
            pageSize: pageSize
        }),
        error: function (m) {
            $('#logs').html('<p><error>查询失败：' + m + '</error></p>');
        },
        success: function (response) {
            var totalCount = response.payload.totalCount;
            $('#totalCount').text(totalCount);
            $('#took').text(response.payload.took + '毫秒');

            if (!!!autoRefreshFlag) {
                $("#logs").html('');
            }
            var logs = response.payload.logs;
            for (var i = 0; i < logs.length; ++i) {
                var message = logs[i].message;
                if (logs[i].msgHighlight) {
                    message = logs[i].msgHighlight;
                }
                message = message.replaceAll('\n', '<br />');
                var exceptionMessage = logs[i].exceptionMessage || '';
                if (logs[i].exMsgHighlight) {
                    exceptionMessage = logs[i].exMsgHighlight;
                }

                var exceptionClass = logs[i].exceptionClass || '';
                var exceptionStack = logs[i].exceptionStack || '';

                var style = 'normal-text';
                var logLevel = logs[i].level;
                if ('ERROR' == logLevel) {
                    style = 'error-text';
                } else if ('WARN' == logLevel) {
                    style = 'warn-text';
                }
                var traceIdSpan = '';
                if (logs[i].traceId) {
                    traceIdSpan = '<span>(' + logs[i].traceId + ')</span>';
                }
                var logp =
                    '<p class="log">' +
                    '<span>' + new Date(logs[i].timestamp).format('yyyy/MM/dd HH:mm:ss,S') + '</span>' +
                    '<span class="log-ip">' + logs[i].ip + '</span>' +
                    '<span>[' + logs[i].threadName + ']</span>' +
                    '<span class="' + style + '">' + logs[i].level + '</span>' +
                    '<span>' + logs[i].clazz + '.' + logs[i].method + '</span>' +
                    traceIdSpan +
                    '<span>' + message + '</span>' +
                    '<span>' + exceptionClass + '</span>' +
                    '<span>' + exceptionMessage + '</span>' +
                    '<br /><span>' + exceptionStack + '</span>' +
                    '</p>';
                $("#logs").append(logp);
                if (autoRefreshFlag) {
                    var oldLogps = $("#logs").children();
                    oldLogps[0].remove();
                }
            }

            totalPage = Math.floor(totalCount / pageSize);
            if (totalPage > 0) {
                $('.pages').css('visibility', 'visible');

                if (currentPage == 0) {
                    $('#firstPage').removeClass('page-enable');
                    $('#firstPage').addClass('page-disable');
                } else {
                    $('#firstPage').removeClass('page-disable');
                    $('#firstPage').addClass('page-enable');
                }
                if (currentPage == totalPage) {
                    $('#lastPage').removeClass('page-enable');
                    $('#lastPage').addClass("page-disable");
                } else {
                    $('#lastPage').removeClass('page-disable');
                    $('#lastPage').addClass("page-enable");
                }

                if (currentPage > 0) {
                    $('#prePage').addClass("page-enable");
                    $('#prePage').removeClass('page-disable');
                } else {
                    $('#prePage').removeClass('page-enable');
                    $('#prePage').addClass("page-disable");
                }

                if (currentPage < totalPage) {
                    $('#nextPage').addClass("page-enable");
                    $('#nextPage').removeClass('page-disable');
                } else {
                    $('#nextPage').removeClass('page-enable');
                    $('#nextPage').addClass("page-disable");
                }
            } else {
                $('.pages').css('visibility', 'hidden');
            }
        }
    });
}

function getInitStartTime() {
    return getTimestamp(new Date()) - 30 * 60 * 1000;
}

function getInitEndTime() {
    return new Date().getTime();
}

function refreshLog() {
    if (window.refreshedMs >= AUTO_REFRESH_MAX_MILLS) {
        $('#autoRefresh').prop('checked', false);
        layui.form.render('checkbox', 'searchFrom');
        stopAutoRefresh();
        return;
    }
    window.refreshedMs += AUTO_REFRESH_INTERVAL;
    var count = AUTO_REFRESH_MAX_SECS - window.refreshedMs / 1000;
    count = count > 0 ? count : '...';
    $('#autoRefreshCounter').html(count);
    initTime();
    searchLogs();
}

function initTime() {
    var startTime = new Date(getInitStartTime()).format('yyyy-MM-dd HH:mm:ss');
    var endTime = new Date().format('yyyy-MM-dd HH:mm:ss');
    layui.laydate.render({
        elem: '#startTime'
        , type: 'datetime'
        , value: startTime
    });
    layui.laydate.render({
        elem: '#endTime'
        , type: 'datetime'
        , value: endTime
    });
}

function startTimer() {
    if (!!!autoRefreshautoRefreshTimer) {
        window.refreshedMs = 0;
        autoRefreshautoRefreshTimer = window.setInterval(refreshLog, AUTO_REFRESH_INTERVAL);
    }
}

function stopTimer() {
    if (autoRefreshautoRefreshTimer) {
        window.refreshedMs = 0;
        window.clearInterval(autoRefreshautoRefreshTimer);
        autoRefreshautoRefreshTimer = null;
    }
}

function startAutoRefresh() {
    autoRefreshFlag = true;
    initTime();
    $('#startTime').attr('disabled', 'disabled');
    $('#startTime').removeClass('enabled-status');
    $('#startTime').addClass('disabled-status');
    $('#endTime').attr('disabled', 'disabled');
    $('#endTime').removeClass('enabled-status');
    $('#endTime').addClass('disabled-status');
    startTimer();
}

function stopAutoRefresh() {
    autoRefreshFlag = false;
    $('#startTime').removeAttr('disabled');
    $('#startTime').removeClass('disabled-status');
    $('#endTime').addClass('enabled-status');
    $('#startTime').removeAttr('disabled');
    $('#endTime').removeClass('disabled-status');
    $('#endTime').addClass('enabled-status');
    $('#autoRefreshCounter').html('...');
    stopTimer();
}

function initForm() {
    layui.use(['form', 'laydate'], function () {
        var form = layui.form;

        initTime();

        form.on('select', function (data) {
            searchLogs();
        });

        form.on('submit(searchButton)', function (data) {
            searchLogs();
            return false;
        });

        form.on('switch(autoRefreshSwitch)', function (data) {
            if (data.elem.checked) {
                startAutoRefresh();
            } else {
                stopAutoRefresh();
            }
        });
    });
}
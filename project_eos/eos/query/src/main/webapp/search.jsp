<%@ page pageEncoding="UTF-8"%>
<html>
<head>
  <meta content="text/html; charset=utf-8" http-equiv="Content-Type"/>
  <title>eos-query</title>
  <link rel="stylesheet" href="js/layui/css/layui.css">
  <link href="css/all.css" rel="stylesheet"/>
</head>
<body>
<div class="mainBody">
  <div class="search-bar">
    <form class="layui-form" action="" lay-filter="searchFrom">
      <div class="layui-form-item">
        <label class="layui-form-label">服务：</label>
        <div class="layui-input-inline">
          <select name="serviceList" id="serviceIds" lay-filter="serviceIds">
            <option value="" disabled="disabled">请选择服务</option>
          </select>
        </div>

        <label class="layui-form-label">关键字：</label>
        <div class="layui-input-inline search-keyword">
          <input type="text" size="50" name="queryString" id="queryString" lay-verify="title" autocomplete="off"
                 class="layui-input">
        </div>

        <label class="layui-form-label">Level：</label>
        <div class="layui-input-inline">
          <select name="logLevel" id="logLevel">
            <option value="ALL">ALL</option>
            <option value="ERROR">ERROR</option>
            <option value="WARN">WARN</option>
            <option value="INFO">INFO</option>
          </select>
        </div>
      </div>
      <div class="layui-form-item">
        <div class="layui-inline">
          <label class="layui-form-label">起始时间：</label>
          <div class="layui-input-inline">
            <input type="text" id="startTime" name="startTime" class="layui-input" placeholder="yyyy-MM-dd HH:mm:ss">
          </div>
        </div>
        <div class="layui-inline">
          <label class="layui-form-label">结束时间：</label>
          <div class="layui-input-inline">
            <input type="text" id="endTime" name="endTime" class="layui-input enabled-status" placeholder="yyyy-MM-dd HH:mm:ss">
          </div>
        </div>

        <div class="layui-inline">
          <button class="layui-btn" lay-submit="" lay-filter="searchButton">
            搜索 <i class="layui-icon layui-icon-search"></i>
          </button>
        </div>

        <div class="layui-inline">
          <label class="layui-form-label" style="width: 120px;">自动刷新 [<span id="autoRefreshCounter">...</span>]：</label>
          <input type="checkbox" name="auto-refresh" id="autoRefresh" lay-skin="switch" lay-text="ON|OFF" lay-filter="autoRefreshSwitch">
        </div>
      </div>
    </form>
  </div>

  <div class="search-result">
    <div class="search-stats-bar">
      <div class="search-stats">
        <span>
          <label>日志总数：</label>
          <label id="totalCount"></label>
        </span>
        <span>
          <label>搜索耗时：</label>
          <label id="took"></label>
        </span>
      </div>

      <div class="pages">
        <span id="firstPage">首页</span>
        <span id="prePage">上一页</span>
        <span id="nextPage">下一页</span>
        <span id="lastPage">尾页</span>
      </div>
    </div>
    <div style="clear:both"></div>

    <div id="logs" class="logs"></div>
  </div>
</div>

</body>
<jsp:include page="routes.jsp" />
<script src="js/jquery.min.js"></script>
<script src="js/layui/layui.js"></script>
<script src="js/common.js"></script>
<script src="js/search.js"></script>
</html>

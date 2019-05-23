<%@ page pageEncoding="UTF-8"%>
<jsp:include page="config.jsp" />
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <title>EOS日志搜索系统</title>
  <link rel="stylesheet" href="${staticContextPath}/js/layui/css/layui.css">
  <link rel="stylesheet" href="${staticContextPath}/css/all.css">
  <link href="https://cdn.jsdelivr.net/npm/ant-design-icons/dist/anticons.min.css" rel="stylesheet">
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
  <div class="layui-header">
    <div class="log-text layui-logo">
      <i class="ai-shop"></i>
      <text>EOS日志搜索系统</text>
    </div>
    <ul class="layui-nav layui-layout-right">
      <li class="layui-nav-item nav-item">
        <a href="javascript:;">
          <i class="layui-icon layui-icon-username"></i>
          <text>您好，管理员</text>
        </a>
        <dl class="layui-nav-child">
          <dd>
            <a href="">
              <i class="layui-icon layui-icon-password"></i>
              <text>安全设置</text>
            </a>
          </dd>
        </dl>
      </li>
      <li class="layui-nav-item">
        <a href="">
          <i class="ai-logout"></i>
          <text>退出登录</text>
        </a>
      </li>
    </ul>
  </div>
  <div class="layui-side layui-bg-black" style="width: 160px;">
    <div class="layui-side-scroll" style="width: 160px;"><!-- 左侧导航区域（可配合layui已有的垂直导航） -->
      <ul class="layui-nav layui-nav-tree" lay-filter="nav" style="width: 160px;">
        <li class="layui-nav-item layui-nav-itemed layui-this">
          <a href="${staticContextPath}/search.jsp" target="mainFrame">
            <i class="layui-icon layui-icon-log"></i>
            <text>日志搜索</text>
          </a>
        </li>
        <li class="layui-nav-item">
          <a href="${staticContextPath}/dashboard.jsp" target="mainFrame">
            <i class="layui-icon layui-icon-chart"></i>
            <text>资源统计</text>
          </a>
        </li>
      </ul>
    </div>
  </div>
  <div class="layui-body" style="overflow: hidden; left: 160px;"><!-- 内容主体区域 -->
    <div id="mainBody">
      <iframe src="${staticContextPath}/search.jsp" width="100%" height="100%" name="mainFrame" scrolling="auto" class="iframe" framborder="0"></iframe>
    </div>
  </div>
  <div class="layui-footer">Copyright©2017-2020 gupaoedu.com All Rights Reserved.</div>
</div>
</body>

<script src="${staticContextPath}/js/layui/layui.js"></script>
<script>
    layui.use('element', function () {
        var element = layui.element;
    });
</script>
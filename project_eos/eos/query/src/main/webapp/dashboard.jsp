<%@ page pageEncoding="UTF-8"%>
<html>
<head>
  <meta content="text/html; charset=utf-8" http-equiv="Content-Type"/>
  <title>eos-query</title>
  <link href="css/all.css" rel="stylesheet"/>
  <link rel="stylesheet" href="js/layui/css/layui.css">
</head>
<body>
<div class="mainBody">
  <div class="layui-card" class="m-20">
    <div class="layui-card-header">日志空间占用</div>
    <div class="layui-card-body">
      <div id="logSizeBar" class="" style="width: 98%; height: 300px"></div>
    </div>
  </div>

  <div class="layui-card" class="m-20">
    <div class="layui-card-header">日志量增长趋势</div>
    <div class="layui-card-body">
      <div id="logSizeLine" class="" style="width: 98%; height: 300px"></div>
    </div>
  </div>

  <!--<div class="block">
    <h2>日志恢复</h2>
    <table id="restoreLogTable">
      <thead>
        <th>日期</th><th>服务</th><th></th>
      </thead>
      <tr>
        <td>2018/08/15</td><td>eos-query</td><td><a href="#">恢复</a></td>
      </tr>
      <tr>
        <td>2018/08/15</td><td>eos-query</td><td><a href="#">恢复</a></td>
      </tr>
    </table>
  </div>-->
</div>
</body>
<jsp:include page="routes.jsp" />
<script src="js/layui/layui.js"></script>
<script src="js/jquery.min.js"></script>
<script src="js/echarts/dist/echarts.js"></script>
<script src="js/common.js"></script>
<script src="js/dashboard.js"></script>
</html>

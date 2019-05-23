<%@ page pageEncoding="UTF-8"%>
<%@page contentType="application/json;charset=utf-8" isErrorPage="true"%>
{"code": 500, "message": "系统内部错误，详细错误：<%=exception.getMessage()%>"}
<%
  exception.printStackTrace();
%>
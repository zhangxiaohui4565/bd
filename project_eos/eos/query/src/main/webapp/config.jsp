<%@page import="com.gupao.bd.eos.query.support.SpringContextUtil"%>
<%@ page import="com.gupao.bd.eos.query.support.StaticConfig" %>
<%
  StaticConfig staticConfig = SpringContextUtil.getBean(StaticConfig.class);
  request.getSession().setAttribute("staticContextPath", staticConfig.getStaticContextPath());
%>


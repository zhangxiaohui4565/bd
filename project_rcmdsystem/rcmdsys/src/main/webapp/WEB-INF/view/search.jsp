<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" rel="stylesheet"
	href="${pageContext.request.contextPath}/css/main.css" />
<title>GP电影院-搜索-${keyword}</title>
</head>
<body>
	<div id="content">
		<div id="search_div">
			<form action="dosearch" method="get">
				<span>GP电影</span> <input type="text" size="300px" width="400px" id="keyword" value="${keyword}">
				<input type="submit" value="搜索" />
			</form>
		</div>
		<div id="search_result_div">
			<h1>
				<span>搜索： ${keyword} </span>
			</h1>
			<div class="subject clearfix">
			<c:forEach var="movie" items="${searchMovies}" varStatus="loop">
				<div class="subject clearfix">
	                <div id="poster_div">
	                    <img alt="${movie.name}" title="${movie.name}" src="${pageContext.request.contextPath}/movieposter/${movie.movieId}.jpg" />
	                </div>
	                <div id="info_div">
	                    <span class="pl">类型:</span>
	                    <c:if test="${not empty movie.tags}">
	                        <c:forEach var="tag" items="${movie.tags}" varStatus="loop">
	                            <c:if test="${loop.index > 0}"> / </c:if>
	                            <span>${tag}</span>
	                        </c:forEach>
	                    </c:if>
	                    <br> <span class="pl">IMDB链接:</span> <a
	                        href="http://www.imdb.com/title/tt${movie.imdbId}"
	                        target="_blank" rel="nofollow">tt${movie.imdbId}</a><br>
	                </div>
	            </div>
			</c:forEach>
			</div>
		</div>
	</div>
</body>
</html>
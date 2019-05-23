<%@page import="java.util.List"%>
<%@page import="javax.servlet.jsp.JspException"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" rel="stylesheet"
	href="${pageContext.request.contextPath}/css/main.css" />
<title>GP电影院</title>
</head>
<body>
	<div id="content">
		<div id="search_div">
			<form action="search">
				<span><a href="/rcmdsys/index">GP电影院 </a></span> <input type="text" width="400px" id="keyword">
				<input type="submit" value="搜索" />
			</form>
		</div>
		<c:if test="${not empty watchedMovies}">
			<div id="watched_movies_div">
				<div class="block_tip">您看过的电影：</div>
				<div>
					<c:forEach var="movie" items="${watchedMovies}" varStatus="loop">
						<a href="${pageContext.request.contextPath}/movie/${movie.movieId}" class="item">
							<div class="cover-wp">
                                <img alt="${movie.name}" title="${movie.name}" src="${pageContext.request.contextPath}/movieposter/${movie.movieId}.jpg" />
							</div>
							<p>${movie.name}(${movie.year})</p>
						</a>
					</c:forEach>
				</div>
			</div>
		</c:if>
		<div id="rcmd_movies_div">
			<div class="block_tip">热门电影：</div>
			<div>
				<c:if test="${not empty rcmdMovies}">
					<c:forEach var="movie" items="${rcmdMovies}" varStatus="loop">
						<a href="${pageContext.request.contextPath}/movie/${movie.movieId}" class="item">
							<div class="cover-wp">
								<img alt="${movie.name}" title="${movie.name}" src="${pageContext.request.contextPath}/movieposter/${movie.movieId}.jpg" />
							</div>
							<p>${movie.name}(${movie.year})</p>
						</a>
					</c:forEach>
				</c:if>
			</div>
		</div>
	</div>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" rel="stylesheet"
	href="${pageContext.request.contextPath}/css/main.css" />
<link type="text/css" rel="stylesheet"
    href="${pageContext.request.contextPath}/css/main.css" />	
<title>GP电影院-${currentMovie.name}</title>
</head>
<body>
	<div id="content">     
	    <div id="search_div">
            <form action="search">
                <span><a href="/rcmdsys/index">GP电影院 </a></span> <input type="text" width="400px" id="keyword">
                <input type="submit" value="搜索" />
            </form>
        </div>
		<div id="current_movie_div">
			<h1>
				<span>${currentMovie.name}</span> <span class="year">(${currentMovie.year})</span>
			</h1>
			<div class="subject clearfix">
				<div id="poster_div">
					<img alt="${currentMovie.name}" title="${currentMovie.name}" src="${pageContext.request.contextPath}/movieposter/${currentMovie.movieId}.jpg" />
				</div>
				<div id="info_div">
					<span class="pl">类型:</span>
					<c:if test="${not empty currentMovie.tags}">
						<c:forEach var="tag" items="${currentMovie.tags}" varStatus="loop">
						    <c:if test="${loop.index > 0}"> / </c:if>
							<span>${tag}</span>
						</c:forEach>
					</c:if>
					<br> <span class="pl">IMDB链接:</span> <a
						href="http://www.imdb.com/title/tt${currentMovie.imdbId}"
						target="_blank" rel="nofollow">tt${currentMovie.imdbId}</a><br>
				</div>
			</div>
		</div>
		<div id="rcmd_movies_div">
			<div class="block_tip">为您推荐（使用${rcmdAlgorithm}算法）：</div>
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
CREATE DATABASE IF NOT EXISTS rcmd_result DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

use rcmd_result;

-- 热销排行
create table alg_topn_movie(
	movie_id int not null,
	view_count int not null
);

-- 看了又看
create table alg_related_movie(
	src_movie_id int not null,
	des_movies varchar(1000) not null
);
create index IDX_mr_movieid on alg_related_movie (src_movie_id); 

-- 基于物品的协同过滤
create table alg_ibcf_movie_similarity(
	src_movie_id int not null,
	des_movie_id int not null,
	pearson double not null,
	cosine double not null,
	jaccard double not null
);
create index IDX_ms_src_movieid on alg_ibcf_movie_similarity (src_movie_id); 
create index IDX_ms_des_movieid on alg_ibcf_movie_similarity (des_movie_id);

-- 用户电影关系表
create table user_watched_movie(
    user_id int not null,
    movie_id int not null,
    rating float not null,
    rate_time long not null
);
create index IDX_uwm_user_id on user_watched_movie (user_id); 

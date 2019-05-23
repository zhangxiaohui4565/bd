## 代码说明
此项目包含数据生成的代码，包括两部分脚本：

1. 根据电影的imdbid从imdb爬取电影封面
2. 将电影数据导入ES

其中 data/movielens 目录下的文件是从 https://grouplens.org/datasets/movielens/ 下载的10k大小的数据，可以根据资源配置情况下载其他大小的数据


## 运行步骤
### 爬取电影封面（在推荐应用系统机器运行）
1. 运行 get\_movie\_poster.py：根据links.csv文件内容，从IMDB抓取电影封面地址，保存到posters.csv文件中，运行命令如下：
```
python get_movie_poster.py MOVIE_DATA_PATH RESULT_PATH，其中 MOVIE_DATA_PATH 为links.csv文件路径，RESULT_PATH为结果保存路径
```
2. 运行 download\_poster.py：从posters.csv文件中获取封面图片url，并下载图片（注意由于网络原因可能不能一次全部下载成功，可以重复运行此脚本，已经下载成功的不会再下载），运行命令如下：
```
python download_movie_poster.py IMG_URL_FILE IMG_PATH，其中IMG_URL_FILE为第一步生成的posters.csv文件路径，IMG_PATH为图片保存路径
```

### 将数据导入ES索引
1. 创建索引 mapping：mapping 内容参考 CODE_BASE\config\es_movie_mapping.json 
2. 修改 ESMovieLoader.java 文件，配置ES索引集群信息
3. 运行 ESMovieLoader
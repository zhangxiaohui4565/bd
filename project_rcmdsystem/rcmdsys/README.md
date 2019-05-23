## 代码说明
此项目包含推荐系统应用系统代码

## 运行方式:
1. 配置  rcmdsys.properties 中的环境信息
2. 打包war并上传到tomcat中
3. 将本地电影封面图片路径映射到tomcat的路径中：修改 tomcat 的 conf/server.xml 文件，在Host标签中添加：
```
<Context docBase="MOVIE_POSTER_BASE_DIR" path="/rcmdsys/movieposter" />
```
其中MOVIE_POSTER_BASE_DIR为电影封面图片的存储路径
3. 启动tomcat，访问： http://localhost:8080/rcmdsys/index
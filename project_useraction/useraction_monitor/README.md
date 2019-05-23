Vue + Express + Echarts + MySQL 搭建可视化展示平台
=========================================

基于vue.js + Express + Echarts + MySQL搭建的可视化展示平台
后端由Express提供接口服务，前端由Vue构建，Demo可以完整的展示出PV和UV

## 文件目录

```
.
├── build ----------------------------------  Webpack文件
├── config ---------------------------------- 编译配置
├── sql ------------------------------------- Mysql初始化文件
└── src
    ├── client
    │   ├── components -----------------------  Vue组件
    │   ├── router ---------------------------  Vue路由
    │   ├── static ---------------------------  静态资源
    │   ├── store ----------------------------  Vuex配置文件
    │   └── views  ---------------------------  由组件拼接的页面
    └── server -------------------------------  服务端
        ├── config ----------------------------  后台配置文件
        ├── dao -------------------------------  DAO层文件
        ├── model ------------------------------ Mode层文件
        ├── public ----------------------------- 静态资源
        ├── router ----------------------------- 后台路由文件
        └── views ------------------------------ 视图文件

```

## 用法

1. 安装依赖包

   `npm install`

2. 运行开发环境

   `npm run dev` 

## 参考资料

Some ideas are stolen from them, really appreciated.

- [Eslint guide](http://eslint.org/docs/user-guide/getting-started)
- [Express generator](http://expressjs.com/en/starter/generator.html)
- [Vue template](https://github.com/vuejs-templates/webpack)
- [Nodemon doc](https://github.com/remy/nodemon#nodemon)
- [Babel register](http://www.ruanyifeng.com/blog/2016/01/babel.html)
- [webpack-dev-middleware-boilerplate](https://github.com/madole/webpack-dev-middleware-boilerplate/tree/master/src)
- [how-can-i-use-webpack-with-express](http://stackoverflow.com/questions/31102035/how-can-i-use-webpack-with-express)
- [The-ultimate-webpack-setup](http://www.christianalfoni.com/articles/2015_04_19_The-ultimate-webpack-setup)
- [southerncross/vue-express-dev-boilerplate](https://github.com/southerncross/vue-express-dev-boilerplate)

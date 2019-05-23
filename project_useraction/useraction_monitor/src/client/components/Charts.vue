<template>
    <div id="chart">
        <div id="chart1">
            <div id="pvChart" :style="{width: '400px', height: '300px'}"></div>
            <div id="uvChart" :style="{width: '400px', height: '300px'}"></div>
        </div>
        <div>
            <div id="visitDurationChart" :style="{width: '800px', height: '450px'}"></div>
        </div>
    </div>
</template>

<style scoped>
    #pvChart {
        float: left;
    }
    #uvChart {
        float: left;
    }
</style>

<script>
export default {
    name: "Charts",
    data () {
        return {

        }
    },
    mounted () {
        this.initPVChart ();
        this.initUVChart ();
        this.initVisitDurationChart ();
    },
    methods: {
        initPVChart () {
            let pvChart = this.$echarts.init(document.getElementById('pvChart'));
            pvChart.setOption({
                title : {
                    text: 'PV统计',
                    x:'center'
                },
                tooltip : {
                    trigger: 'item',
                    formatter: "{b}, PV={c} ({d}%)"
                },
                series : [
                    {
                        type: 'pie',
                        radius : '55%',
                        center: ['50%', '50%'],
                        itemStyle: {
                            emphasis: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        }
                    }
                ]
            });
            //显示加载动画
            pvChart.showLoading();
            this.$http.get('./api/chart/pv').then((res) => {
                let data = JSON.parse(JSON.stringify(res.data));
                console.log(data);
                pvChart.hideLoading();
                pvChart.setOption({
                    series: [{
                        data: data
                    }]
                });
            })
        },
        initUVChart () {
            let uvChart = this.$echarts.init(document.getElementById('uvChart'));
            uvChart.setOption({
                title : {
                    text: 'UV统计',
                    x:'center'
                },
                tooltip : {
                    trigger: 'item',
                    formatter: "{b}, UV={c} ({d}%)"
                },
                series : [
                    {
                        type: 'pie',
                        radius : '55%',
                        center: ['50%', '50%'],
                        itemStyle: {
                            emphasis: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        }
                    }
                ]
            });
            //显示加载动画
            uvChart.showLoading();
            this.$http.get('./api/chart/uv').then((res) => {
                let data = JSON.parse(JSON.stringify(res.data));
                console.log(data);
                uvChart.hideLoading();
                uvChart.setOption({
                    series: [{
                        data: data
                    }]
                });
            })
        },
        initVisitDurationChart () {
            let visitDurationChat = this.$echarts.init(document.getElementById('visitDurationChart'));
            visitDurationChat.setOption({
                title : {
                    text: '平均停留时间统计',
                    x:'center'
                },
                xAxis: {
                    type: 'category',
                },
                yAxis: {
                    type: 'value'
                },
                series: [{
                    name:'平均停留时间',
                    type: 'bar'
                }]
            });
            //显示加载动画
            visitDurationChat.showLoading();
            this.$http.get('./api/chart/visit_duration').then((res) => {
                let data = JSON.parse(JSON.stringify(res.data));
                console.log(data);

                visitDurationChat.hideLoading();
                visitDurationChat.setOption({
                    xAxis: {
                        data: data.map(x => x["name"])
                    },
                    series: [{
                        data: data.map(x => x["value"])
                    }]
                });
            })
        }
    }
}
</script>
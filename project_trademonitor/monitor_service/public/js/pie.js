//功能：绘制饼图，客群分析

var pieData1 = [{value: 335, name: '新客'},
    {value: 310, name: '老客'}];


var pieData = [];

for (var i = 0; i < pieData1.length; i++) {
    pieData.push({"name": pieData1[i].name, "value": 0});
}


pieOption = {
    color: ['#61a0a8', '#d48265', '#91c7ae', '#749f83', '#ca8622', '#bda29a', '#6e7074', '#546570', '#c4ccd3'],
    title: {
        text: '客群分析',
        // subtext: '新老客占比',
        x: 'center',
        textStyle: {
            color: "#C0C0C0",
            fontWeight: "normal",
            fontSize:14
        }
    },
    tooltip: {
        trigger: 'item',
        formatter: "{a} <br/>{b} : {c} ({d}%)"
    },
    series: [
        {
            name: '客群',
            type: 'pie',
            radius: '55%',
            // center: ['40%', '50%'],
            // data: data.seriesData,
            data: pieData,
            itemStyle: {
                emphasis: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            },
            label: {
                formatter: "{b} : {c} \n ({d}%)"
                // position: 'inner'
            }
        }
    ]
};




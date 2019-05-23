
barOption = {
    title: {
        text: '各产品成交额',
        left: 'center',
        textStyle: {
            color: '#C0C0C0',
            fontWeight: "normal",
            fontSize:14
        }
    },
    tooltip: {
        trigger: 'axis'
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis: [{
        type: 'category',
        data: ['iPhone', '小米', 'OPPO','华为'],
        splitLine: {
            show: false
        },
        axisTick: {
            alignWithLabel: true
        },
        axisLine: {
            lineStyle: {
                type: 'solid',
                color: '#fff',//左边线的颜色
                width:'1'//坐标线的宽度
            }
        },
        axisLabel: {
            textStyle: {
                color: '#fff',//坐标值得具体的颜色

            }
        }
    }],
    yAxis: [{
        type: 'value',
        splitLine: {
            show: false
        },
        axisLine: {
            lineStyle: {
                type: 'solid',
                color: '#fff',//左边线的颜色
                width:'1'//坐标线的宽度
            }
        },
        axisLabel: {
            textStyle: {
                color: '#fff',//坐标值得具体的颜色

            }
        }
        //splitArea: {
        //    show: true
        //}
    }],
    series: [{
        name: '成交额',
        type: 'bar',
        label: {
            normal: {
                show: true,
                position: 'top'
            }
        },
        itemStyle: {
            normal: {
                // 定制显示（按顺序）
                color: function(params) {
                    var colorList = ['#C33531','#EFE42A','#64BD3D','#EE9201','#29AAE3', '#B74AE5','#0AAF9F','#E89589','#16A085','#4A235A','#C39BD3 ','#F9E79F','#BA4A00','#ECF0F1','#616A6B','#EAF2F8','#4A235A','#3498DB' ];
                    return colorList[params.dataIndex]
                }
            }
        },
        data: [0,0,0,0]
    }]
};

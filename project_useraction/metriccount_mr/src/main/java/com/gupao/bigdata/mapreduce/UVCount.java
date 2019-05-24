package com.gupao.bigdata.mapreduce;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;

import java.io.IOException;

/**
 * 用户行为分析统计页面PV
 * 思路：两个JOB。第一个Job按照product_id+uid进行去重；第二个Job获得第一个Job去重后的结果取出product_id，然后按照WordCount思路执行
 * 输入数据Example
 * 127.0.0.1	http://opencart.gp-bd.com/index.php?route=product/product/review&product_id=41	http://opencart.gp-bd.com/index.php?route=product/product&path=20_27&product_id=41	uid=0100007F7588435B8A04CF4302030303	10/Jul/2018:00:10:16 +0800
 * */
public class UVCount {

    /**
     * 第一个Job的Mapper
     * */
    public static class UVDistinctMapper extends Mapper<Object, Text, Text, NullWritable> {

        private Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            // 按照tab切分每一行日志
            String[] rawLogFields = value.toString().split("\t");
            // 获得productId
            String accessURL = rawLogFields[1];
            if (StringUtils.isNotEmpty(accessURL)) {
                MultiMap<String> values = new MultiMap<String>();
                UrlEncoded.decodeTo(accessURL, values, "UTF-8");
                String productId = values.getValue("product_id", 0);
                // 获得userId
                String cookie = rawLogFields[3];
                String userId = cookie.contains("uid=") ? cookie.split("=")[1] : null;
                if (StringUtils.isNumeric(productId) && StringUtils.isNotEmpty(userId)) {
                    // 使用productId和userId作为主键，value设置为null
                    word.set(productId + "\t" + userId);
                    context.write(word, NullWritable.get());
                }
            }
        }
    }

    /**
     * 第一个Job的Reducer
     * */
    public static class UVDistinctReducer extends Reducer<Text, NullWritable, Text, NullWritable> {
        public void reduce(Text key, Iterable<NullWritable> values,
                           Context context) throws IOException, InterruptedException {
            // 直接输出结果，MapReduce框架已经帮助我们对key去重了
            context.write(key, NullWritable.get());
        }
    }

    /**
     * 第二个Job的Mapper
     * */
    public static class UVCountMapper extends Mapper<Object, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            // 使用tab作为分隔符，切分第一个Job去重后的key：product_Id \t userId
            String[] dataFields = value.toString().split("\t");
            // 只需要取出productId即可，因为我们统计的是产品的pv
            word.set(dataFields[0]);
            // 输出kv对，key为productId，value为1
            context.write(word, one);
        }
    }

    /**
     * 第二个Job的Reducer
     * */
    public static class UVCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context) throws IOException, InterruptedException {
            // 按照WordCount思路进行累加处理
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }

    /**
     * man方法，需要传入三个参数，第一个job的输入路径，第一个job的输出路径，第二个job的输出路径
     * */
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        // 配置Job1
        Job job1 = Job.getInstance(conf, "opencart uv phase1 distinct");
        job1.setJarByClass(UVCount.class);
        job1.setMapperClass(UVDistinctMapper.class);
        job1.setReducerClass(UVDistinctReducer.class);
        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(NullWritable.class);
        FileInputFormat.addInputPath(job1, new Path(args[0]));
        FileOutputFormat.setOutputPath(job1, new Path(args[1]));
        // 进行项目启动
        job1.waitForCompletion(true);

        // 配置Job2，Job2的输入数据是Job1的输出
        Job job2 = Job.getInstance(conf, "opencart uv phase2 count");
        job2.setJarByClass(UVCount.class);
        job2.setMapperClass(UVCountMapper.class);
        job2.setReducerClass(UVCountReducer.class);
        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job2, new Path(args[1]));
        FileOutputFormat.setOutputPath(job2, new Path(args[2]));
        job2.waitForCompletion(true);
    }

}

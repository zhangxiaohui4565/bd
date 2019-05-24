package com.gupao.bigdata.mapreduce;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
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
 * 思路和WordCount一致
 * 输入数据Example
 * 127.0.0.1	http://opencart.gp-bd.com/index.php?route=product/product/review&product_id=41	http://opencart.gp-bd.com/index.php?route=product/product&path=20_27&product_id=41	uid=0100007F7588435B8A04CF4302030303	10/Jul/2018:00:10:16 +0800
 * */
public class PVCount {

    /**
     * PV统计Mapper方法
     * */
    public static class PVMapper extends Mapper<Object, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();
        // key 表示偏移量   value表示每行的内容
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            // 使用tab分隔符对nginx的日志进行切分
            String[] rawLogFields = value.toString().split("\t");
            // 拿到访问的url： 例如 http://opencart.gp-bd.com/index.php?route=product/product/review&product_id=41
            String accessURL = rawLogFields[1];
            if (StringUtils.isNotEmpty(accessURL) && accessURL.contains("opencart.gp-bd.com")) {
                // 使用UrlEncoded进行解析product_id
                //{product_id=41,  http://opencart.gp-bd.com/index.php?route=product/product/review}
                MultiMap<String> values = new MultiMap<String>();
                UrlEncoded.decodeTo(accessURL, values, "UTF-8");
                String productId = values.getValue("product_id", 0);
                // 如果有product_id这个参数，说明访问了这个页面
                if (StringUtils.isNumeric(productId)) {
                    // key为product_id，value为访问次数1次
                    word.set(productId);
                    context.write(word, one);
                }
            }
        }
    }

    /**
     * PV统计Reducer方法
     * 参数类型   输入k,v 输出k,v
     * */
    public static class PVReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        /**
         *
         * @param key   根据key进行partition
         * @param values  同一个key的值的集合
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context) throws IOException, InterruptedException {
            // 按照WordCount思路对相同key的value进行累加即可获得页面点击次数
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            // 进行输出  与python写的print 一致
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "opencart user action pv count");
        job.setJarByClass(PVCount.class);
        job.setMapperClass(PVMapper.class);
        job.setCombinerClass(PVReducer.class);
        job.setReducerClass(PVReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}

package com.gupao.bigdata.mapreduce.topn;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class TopNDriver extends Configured implements Tool {

    private static Logger LOGGER = Logger.getLogger(TopNDriver.class);

    public int run(String[] args) throws Exception {
        Job job = new Job(getConf());

        job.setJarByClass(TopNDriver.class);

        int N = Integer.parseInt(args[0]); // top N
        job.getConfiguration().setInt("N", N);
        job.setJobName("TopNDriver");

        job.setMapperClass(TopNMapper.class);
        job.setReducerClass(TopNReducer.class);
        job.setNumReduceTasks(1);

        // map() output (K,V)
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);

        // reduce() output (K,V)
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        // args[1] = input directory
        // args[2] = output directory
        FileInputFormat.setInputPaths(job, new Path(args[1]));
        FileOutputFormat.setOutputPath(job, new Path(args[2]));

        boolean status = job.waitForCompletion(true);
        LOGGER.info("run(): status="+status);
        return status ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            LOGGER.warn("usage TopNDriver <N> <input> <output>");
            System.exit(1);
        }

        LOGGER.info("N=" + args[0]);
        LOGGER.info("inputDir=" + args[1]);
        LOGGER.info("outputDir=" + args[2]);
        int returnStatus = ToolRunner.run(new TopNDriver(), args);
        System.exit(returnStatus);
    }

}

package com.gupao.bigdata.mapreduce.topn;

import java.io.IOException;
import java.util.PriorityQueue;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

public class TopNReducer extends Reducer<NullWritable, Text, NullWritable, Text> {

    private int N;

    private PriorityQueue<Item> priorityQueue = new PriorityQueue();

    private static Logger LOGGER = Logger.getLogger(TopNReducer.class);

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        this.N = context.getConfiguration().getInt("N", 5);
    }

    @Override
    protected void reduce(NullWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for (Text value : values) {
            LOGGER.info("Debug: text = [" + value.toString() + "]");
            String[] words = value.toString().split("\t");
            Long count = Long.parseLong(words[1]);
            Item item = new Item(count, value.toString());
            if (priorityQueue.size() < N || count > priorityQueue.peek().getCount()) {
                priorityQueue.offer(item);
            }
            if (priorityQueue.size() > N) {
                priorityQueue.poll();
            }
        }
        for (Item topN : priorityQueue) {
            context.write(NullWritable.get(), new Text(topN.getContent()));
        }
    }

}

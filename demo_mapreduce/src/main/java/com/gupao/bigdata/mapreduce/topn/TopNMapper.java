package com.gupao.bigdata.mapreduce.topn;

import java.io.IOException;
import java.util.PriorityQueue;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

public class TopNMapper extends Mapper<Object, Text, NullWritable, Text> {

    private int N;
    private PriorityQueue<Item> priorityQueue = new PriorityQueue();

    private static Logger LOGGER = Logger.getLogger(TopNMapper.class);

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        this.N = context.getConfiguration().getInt("N", 5);
    }

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String[] words = value.toString().split("\t");
        if (words.length < 2) {
            return;
        }
        LOGGER.info("Debug: Map get text = [" + value.toString() + "]");
        Long count = Long.parseLong(words[1]);
        Item item = new Item(count, value.toString());
        if (priorityQueue.size() < N || count > priorityQueue.peek().getCount()) {
            priorityQueue.offer(item);
        }
        if (priorityQueue.size() > N) {
            priorityQueue.poll();
        }
    }
    @Override
    public void cleanup(Context context) throws IOException, InterruptedException {
        for (Item item : priorityQueue) {
            LOGGER.info("Debug: Map write to context = [" + item.getContent().toString() + "]");
            context.write(NullWritable.get(), new Text(item.getContent()));
        }
    }

    public static void main(String[] args) {

    }

}


package com.gupao.bigdata.mapreduce.topn;

import java.io.IOException;
import java.util.PriorityQueue;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

public class TopNMapper extends Mapper<Object, Text, NullWritable, Text> {
    /**
     * NullWritable是Writable的一个特殊类，实现方法为空实现，不从数据流中读数据，也不写入数据，只充当占位符，
     * 如在MapReduce中，
     * 如果你不需要使用键或值，你就可以将键或值声明为NullWritable,NullWritable是一个不可变的单实例类型。
     */
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
            //添加元素
            priorityQueue.offer(item);
        }
        if (priorityQueue.size() > N) {
            // 取出元素不进行删除  peek()
            // 取出元素并删除
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


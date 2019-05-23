package com.gupao.bigdata.mapreduce.topn;

import org.apache.hadoop.io.Text;

public class Item implements Comparable<Item>{

    private Long count;

    private String content;

    public Item() {
    }

    public Item(Long count, String content) {
        this.count = count;
        this.content = content;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int compareTo(Item o) {
        return Long.compare(count, o.getCount());
    }
}

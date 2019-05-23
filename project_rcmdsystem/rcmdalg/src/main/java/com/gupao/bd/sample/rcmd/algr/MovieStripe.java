package com.gupao.bd.sample.rcmd.algr;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

public class MovieStripe extends MapWritable {
    public MovieStripe(){
        super();
    }

    /**
     * 添加putAll方法，实现两个MovieStripe的相加
     * @param ts
     */
    public void putAll(MovieStripe ts) {
        for(Entry<Writable, Writable> entry : ts.entrySet()) {
            Text movieId = (Text)entry.getKey();
            IntWritable count = (IntWritable)entry.getValue();
            //如果已包含该健，累加值
            if(this.containsKey(movieId)) {
                int newValue = ((IntWritable)this.get(movieId)).get() + count.get();
                this.put(movieId, new IntWritable(newValue));
            } else { //如果不包含该健，则加上
                this.put(movieId, count);
            }
        }
    }
}

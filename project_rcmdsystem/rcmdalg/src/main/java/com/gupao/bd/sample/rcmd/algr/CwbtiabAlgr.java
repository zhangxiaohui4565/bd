package com.gupao.bd.sample.rcmd.algr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.google.gson.Gson;

/**
 * CWBTIAB算法MapReduce实现
 * 
 * @author george
 *
 */
public class CwbtiabAlgr {

	public static void main(String[] args) throws Exception {
		if (args == null || args.length != 3) {
			System.out.println("使用方式： com.gupao.bd.sample.rcmd.algr.CwbtiabAlgr [源数据地址] [第一次输出地址] [结果输出地址]");
			return;
		}

        Configuration conf = new Configuration();
        System.out.println("开始第一个任务");
        Job job1 = Job.getInstance(conf, "flat data");
		job1.setJarByClass(CwbtiabAlgr.class);
		job1.setMapperClass(FirstMapper.class);
		job1.setMapOutputKeyClass(Text.class);
		job1.setMapOutputValueClass(Text.class);
		job1.setReducerClass(FirstReducer.class);
		job1.setOutputKeyClass(Text.class);
		job1.setOutputValueClass(Text.class);

		FileInputFormat.addInputPath(job1, new Path(args[0]));
		FileOutputFormat.setOutputPath(job1, new Path(args[1]));
		job1.waitForCompletion(true);
        System.out.println("第一个MR结束");

		// 第二个job的配置
        System.out.println("开始第二个任务");
		Job job2 = new Job(conf, "count");
		job2.setJarByClass(CwbtiabAlgr.class);
		job2.setMapperClass(SecondMapper.class);
		job2.setReducerClass(SecondReducer.class);
		job2.setMapOutputKeyClass(Text.class);
		job2.setMapOutputValueClass(MovieStripe.class);
		job2.setOutputKeyClass(Text.class);
		job2.setOutputValueClass(Text.class);

		FileInputFormat.addInputPath(job2, new Path(args[1]));
		FileOutputFormat.setOutputPath(job2, new Path(args[2]));
		job2.waitForCompletion(true);
        System.out.println("第二个MR结束");
	}
}

class FirstMapper extends Mapper<Object, Text, Text, Text> {
	public Text userId = new Text();
	public Text movieId = new Text();

	@Override
	protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		// 输入数据格式：userid,movieid,rating,timestamp
		String[] words = StringUtils.split(value.toString(), ",");
		userId.set(words[0]);
		movieId.set(words[1]);
		context.write(userId, movieId);
	}
}

class FirstReducer extends Reducer<Text, Text, NullWritable, Text> {
	@Override
	protected void reduce(Text key, Iterable<Text> ite, Context context) throws IOException, InterruptedException {
		context.write(NullWritable.get(), new Text(StringUtils.join(ite.iterator(), ",")));
	}
}

class SecondMapper extends Mapper<Object, Text, Text, MovieStripe> {

	@Override
	protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		// 输入数据格式：movieid1,movieid2,movieid3...
		// 输出数据格式：
		//		movieid1,[(movieid2,1),(movieid3,1)...]
		//		movieid2,[(movieid1,1),(movieid3,1)...]
		String[] movieIds = StringUtils.split(value.toString(), ",");
		for (int i = 0; i < movieIds.length; i++) {
			MovieStripe termTS = new MovieStripe();
			for (int j = 0; j < movieIds.length; j++) {
				// 防止重复
				if(!movieIds[j].equals(movieIds[i])) {
					Text movieId = new Text(movieIds[j]);
					termTS.put(movieId, new IntWritable(1));
				}
			}
			Text srcMovieId = new Text(movieIds[i]);
			context.write(srcMovieId, termTS);
		}
	}
}

class SecondReducer extends Reducer<Text, MovieStripe, Text, Text> {

	private final static int N = 10;

	@Override
	protected void reduce(Text key, Iterable<MovieStripe> ite, Context context)
			throws IOException, InterruptedException {
		// 将关联的电影合并，对次数求和
		MovieStripe sumStripe = new MovieStripe();
		for (MovieStripe i : ite) {
			sumStripe.putAll(i);
		}
		
		// 提取原始数据
		List<MovieCount> movies = new ArrayList<>();
		for (Entry<Writable, Writable> movieCountPair : sumStripe.entrySet()) {
			movies.add(new MovieCount(((Text) movieCountPair.getKey()).toString(),
					((IntWritable) movieCountPair.getValue()).get()));
		}
		
		// 按照次数排序并取出topN
		Collections.sort(movies);
		List<MovieCount> topnMovies;
		if (movies.size() > N) {
			topnMovies = movies.subList(0, N);
		} else {
			topnMovies = movies;
		}
		Gson gson = new Gson();
		context.write(key, new Text(gson.toJson(topnMovies)));
	}
}
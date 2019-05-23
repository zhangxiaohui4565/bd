package com.gupao.bd.sample.hdfs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * 用于生成大量小文件
 * @author liubo
 *
 */
public class LoopWrite {

	public static void main(String[] args) throws Exception{
		if(args == null || args.length != 3){
			System.out.println("使用方式： com.gupao.bd.sample.hdfs.LoopWrite [本地文件地址] [hdfs根目录地址] [文件数量]");
			return;
		}
		
		Configuration conf = new Configuration();
		try {
			conf.set("fs.defaultFS", "hdfs://master:8020");
			FileSystem fs = FileSystem.get(conf);
			System.out.println("获取到FileSystem实例：" + fs.getClass());
			String inFilePath = args[0];
			File inFile = new File(inFilePath);
			if (!inFile.exists()) {
				System.out.println("Output file already exists: " + inFilePath);
				throw new IOException("Output file already exists");
			}

			int fileSize = Integer.valueOf(args[2]);
			long start = System.currentTimeMillis();
			System.out.println("开始写入文件");
			for(int i =0; i<fileSize; i++){
				Path outFile = new Path(args[1] + "/" + i);
				// open and read from file
				InputStream in = new BufferedInputStream(new FileInputStream(inFilePath));
				// Create file to write
				FSDataOutputStream out = fs.create(outFile);
				byte buffer[] = new byte[256];
				try {
					int bytesRead = 0;
					while ((bytesRead = in.read(buffer)) > 0) {
						out.write(buffer, 0, bytesRead);
					}
				} catch (IOException e) {
					System.out.println("Error while copying file");
				} finally {
					in.close();
					out.close();
				}
			}
			System.out.println("结束写入文件，总共时间:" + (System.currentTimeMillis() - start)/1000);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
package com.gupao.bd.sample.hdfs;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * HDFS 读文件
 * @author george
 *
 */
public class HDFSFileRead {

	public static void main(String[] args) {
		if (args == null || args.length != 1) {
			System.out.println("使用方式： com.gupao.bd.sample.hdfs.HDFSFileRead [hdfs目的文件地址]");
			return;
		}

		Configuration conf = new Configuration();
		try {
			// 指定hdfs入口地址
			conf.set("fs.defaultFS", "hdfs://master:8020");
			FileSystem fs = FileSystem.get(conf);

			// 指定读取文件地址
			Path targetFile = new Path(args[0]);
			if (!fs.exists(targetFile)) {
				System.out.println("Input file not found:" + args[0]);
				throw new IOException("Input file not found:" + args[0]);
			}

			// 指定输出流
			FSDataInputStream in = fs.open(targetFile);
			OutputStream out = System.out;
			byte buffer[] = new byte[256];
			try {
				int bytesRead = 0;
				while ((bytesRead = in.read(buffer)) > 0) {
					out.write(buffer, 0, bytesRead);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				in.close();
				out.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
/**
 * 
 */
package com.gupao.bd.sample.rcmd.data;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

/**
 * 将电影数据导入ES。
 * 注意：在运行此导入程序前，需先设置索引mapping，mapping内容为：CODE_BASE/config/es_movie_mapping.json
 * 
 * @author george
 *
 */
public class ESMovieLoader {

	// 根据ES集群信息修改如下配置：
	private static String ES_CLUSTER_ADDR = "106.15.195.16";
	private static String INDEX_NAME = "gp";
	private static String INDEX_TYPE_NAME = "movie";
	private static String CLUSTER_NAME = "gp-es";

	private static int INDEX_BATCH_SIZE = 100;

	public static void main(String[] args) throws Exception {

		// 读取 movies.csv
		List<String> lines = Files.readLines(new File("./data/movielens/movies.csv"), Charsets.UTF_8);

		// 电影封面图片地址通过执行：get_movie_poster.py 程序获取到，将数据保存在./data/crawldata/posters.csv目录中
		List<String> moviesImgUrl = Files.readLines(new File("./data/crawldata/posters.csv"), Charsets.UTF_8);
		Map<String, String> moviesImgMap = new HashMap<>();
		for (String imgUrl : moviesImgUrl) {
			String[] elements = imgUrl.split(",");
			moviesImgMap.put(elements[0], elements[1]);
		}

		// 读取 links.csv 文件获取imdb与movieid的映射关系
		List<String> links = Files.readLines(new File("./data/movielens/links.csv"), Charsets.UTF_8);
		Map<String, String> imdbIdMap = new HashMap<>();
		for (String line : links) {
			String[] elements = line.split(",");
			imdbIdMap.put(elements[0], elements[1]);
		}

		Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
		TransportClient client = new PreBuiltTransportClient(settings);
		client.addTransportAddress(new TransportAddress(new InetSocketAddress(ES_CLUSTER_ADDR, 9300)));

		// 解析电影数据，批量写入es
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		int bufferedCount = 0;
		int i = 0;
		for (String line : lines) {
			if (i == 0) {
				// 去掉标题行
				i++;
				continue;
			}

			Map<String, Object> toIndexProperties = parseMovieInfo(line, moviesImgMap, imdbIdMap);
			if(toIndexProperties == null) {
				// 电影信息解析错误，格式不正确
				continue;
			}
			String strId = String.valueOf(toIndexProperties.get("id"));
			bulkRequest.add(client.prepareIndex(INDEX_NAME, INDEX_TYPE_NAME, strId).setSource(toIndexProperties));
			bufferedCount++;
			if (bufferedCount >= INDEX_BATCH_SIZE) {
				BulkResponse bulkResponse = bulkRequest.get();
				if (bulkResponse.hasFailures()) {
					System.out.println("批量写入失败：" + bulkResponse.buildFailureMessage());
				} else {
					System.out.println("批量写入数据，批量条数：" + bufferedCount);
				}
				bulkRequest = client.prepareBulk();
				bufferedCount = 0;
			}
		}

		BulkResponse bulkResponse = bulkRequest.get();
		if (bulkResponse.hasFailures()) {
			System.out.println(bulkResponse.buildFailureMessage());
		}
		client.close();
	}

	static Map<String, Object> parseMovieInfo(String line, Map<String, String> moviesImgMap,
			Map<String, String> imdbIdMap) {
		try {
			Map<String, Object> toIndexProperties = new HashMap<>();
			line = line.replace(", ", "COMMA ").replace("\"", "");
			String[] properties = line.split(",");
			String idStr = properties[0];
			int id = Integer.valueOf(idStr);
			String nameAndYear = properties[1].trim();
			String name = nameAndYear.substring(0, nameAndYear.length() - 6);
			int year = Integer.valueOf(nameAndYear.substring(name.length() + 1, nameAndYear.length() - 1));
			List<String> tags = Arrays.asList(properties[2].split("\\|"));

			toIndexProperties.put("id", id);
			String originName = name.replace("COMMA ", ", ").trim();
			toIndexProperties.put("name", originName);
			toIndexProperties.put("year", year);
			toIndexProperties.put("tags", tags);
			toIndexProperties.put("image_url", moviesImgMap.get(idStr));
			toIndexProperties.put("imdb_id", imdbIdMap.get(idStr));
			return toIndexProperties;
		} catch (Exception e) {
			System.out.println(String.format("数据解析错误，当前处理内容[%s]，报错信息：[%s]", line, e.getMessage()));
			return null;
		}
	}
}

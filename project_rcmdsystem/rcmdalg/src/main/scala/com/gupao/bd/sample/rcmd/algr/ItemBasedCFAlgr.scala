package com.gupao.bd.sample.rcmd.algr;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;

/**
 * 基于物品的协同过滤电影推荐算法
 * 
 * @author george
 *
 */
object ItemBasedCFAlgr {

  def main(args: Array[String]): Unit = {
    if (args.size != 2) {
      println("使用方式： com.gupao.bd.sample.rcmd.algr.ItemBasedCFAlgr [源数据地址] [结果输出地址]")
      sys.exit(1)
    }
    
    val input = args(0)
    val output = args(1)

    val sparkConf = new SparkConf().setAppName("ItemBasedCFAlgr")
    val sc = new SparkContext(sparkConf)
    val usersRatings = sc.textFile(input)

    // 输入数据格式：userid,movieid,rating,timestamp
    val userMovieRating = usersRatings.filter(!_.contains("user")).map(line => {
         val tokens = line.split(",")
         (tokens(0), tokens(1), tokens(2).toFloat)
    })

    // 计算每个电影的评论人数
    val numberOfRatersPerMovie = userMovieRating.map(umr => (umr._2, 1)).reduceByKey(_ + _)

    // 对每个用户看过的电影分组
    val groupedByUser = userMovieRating
      .map(umr => (umr._2, (umr._1, umr._3)))  // 输出：<movieid, (userid, rating)>
      .join(numberOfRatersPerMovie)  // 输出：<movieid, ((userid, rating), ratingCount)>
      .map(tuple => (tuple._2._1._1, (tuple._1, tuple._2._1._2, tuple._2._2))) // 输出：<userid, (movieid, rating, ratingCount)>
      .groupByKey() // 输出：<userid, [(movieid, rating, ratingCount), (movieid, rating, ratingCount)....]>

    // 根据用户看过的电影收集共现电影对，并计算相应的分数
    val moviePairs = groupedByUser.flatMap(tuple => {
      val sortedMovies = tuple._2.toList.sortBy(f => f._1) //对每个用户的电影列表按照id排序，其中元素的结构：(movieid, rating, ratingCount)
      val moviePair = for {
        firstMovie <- sortedMovies
        secondMovie <- sortedMovies
        // 防止重复
        if (firstMovie._1 < secondMovie._1);
        ratingProduct = firstMovie._2 * secondMovie._2
        firstMovieRatingSquared = firstMovie._2 * firstMovie._2
        secondMovieRatingSquared = secondMovie._2 * secondMovie._2
      } yield {
        ((firstMovie._1, secondMovie._1), (firstMovie._2, firstMovie._3, secondMovie._2, secondMovie._3, ratingProduct, firstMovieRatingSquared, secondMovieRatingSquared))
      }
      moviePair
    })
    val groupedMoviePairs = moviePairs.groupByKey()

    // 计算电影之间的相似度
    val result = groupedMoviePairs.mapValues(itr => {
      // 对两个电影同时评分过的用户数
      val groupSize = itr.size
      
      // 将对应的计算值合并到列表中以进行统计计算
      val (firstMovieRatings, firstMovieRaterCounts, secondMovieRatings, secondMovieRaterCounts, ratingProducts, firstMovieRatingSqs, secondMovieRatingSqs) =
        itr.foldRight((List[Float](), List[Int](), List[Float](), List[Int](), List[Float](), List[Float](), List[Float]()))((a, b) =>
          (
            a._1 :: b._1,
            a._2 :: b._2,
            a._3 :: b._3,
            a._4 :: b._4,
            a._5 :: b._5,
            a._6 :: b._6,
            a._7 :: b._7))
      // 计算统计值
      val ratingProductSum = ratingProducts.sum
      val firstMovieRatingSum = firstMovieRatings.sum
      val secondMovieRatingSum = secondMovieRatings.sum
      val firstMovieRatingSqSum = firstMovieRatingSqs.sum
      val secondMovieRatingSqSum = secondMovieRatingSqs.sum
      val firstMovieRaterCount = firstMovieRaterCounts.max
      val secondMovieRaterCount = secondMovieRaterCounts.max

      // 余弦相似度
      val cosineSimilarity = ratingProductSum / (math.sqrt(firstMovieRatingSqSum) * math.sqrt(secondMovieRatingSqSum))
      
      // 皮尔逊相似度
      val numerator = groupSize * ratingProductSum - firstMovieRatingSum * secondMovieRatingSum
      val denominator = math.sqrt(groupSize * firstMovieRatingSqSum - firstMovieRatingSum * firstMovieRatingSum) *
        math.sqrt(groupSize * secondMovieRatingSqSum - secondMovieRatingSum * secondMovieRatingSum)
      val pearsonSimilarity = if(denominator == 0){
        0
      } else {
    	  numerator / denominator
      }

      // 杰卡德相似度
      val jaccardSimilarity = groupSize.toDouble / (firstMovieRaterCount + secondMovieRaterCount - groupSize)

      // 返回结果
      (pearsonSimilarity, cosineSimilarity, jaccardSimilarity)
    })

    // 保存结果: movieid1, movieid2, pearsonSimilarity, cosineSimilarity, jaccardSimilarity
    result.map(tuple => {
      tuple._1._1 + "," + tuple._1._2 + "," + tuple._2._1 + "," + tuple._2._2 + "," + tuple._2._3
      }).saveAsTextFile(output)

    sc.stop()
  }
}
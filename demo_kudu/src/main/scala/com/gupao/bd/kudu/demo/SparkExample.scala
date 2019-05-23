package com.gupao.bd.kudu.example

import org.apache.kudu.client.CreateTableOptions
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

import collection.JavaConverters._


object SparkExample extends App {

  // Read a table from Kudu
  val sqlContext = getSparkSession("xx")

  val df = sqlContext.read.options(Map("kudu.master" -> "kudu.master:7051", "kudu.table" -> "kudu_table")).load()

  // ...or register a temporary table and use SQL
  df.createOrReplaceTempView("kudu_table")
  val filteredDF = sqlContext.sql("select id from kudu_table where id >= 5")

  filteredDF.show()

  // Use KuduContext to create, delete, or write to Kudu tables
  val kuduContext = new KuduContext("kudu.master:7051")

  // Create a new Kudu table from a dataframe schema
  // NB: No rows from the dataframe are inserted into the table
  kuduContext.createTable(
    "test_table", df.schema, Seq("key"),
    new CreateTableOptions()
      .setNumReplicas(1)
      .addHashPartitions(List("key").asJava, 3))

  // Insert data
  kuduContext.insertRows(df, "test_table")

  // Delete data
  kuduContext.deleteRows(filteredDF, "test_table")

  // Upsert data
  kuduContext.upsertRows(df, "test_table")

  // Update data
  val alteredDF = df.select("id", $"count" + 1)
  kuduContext.updateRows(alteredDF, "test_table")

  // Data can also be inserted into the Kudu table using the data source, though the methods on KuduContext are preferred
  // NB: The default is to upsert rows; to perform standard inserts instead, set operation = insert in the options map
  // NB: Only mode Append is supported
  df.write.options(Map("kudu.master" -> "kudu.master:7051", "kudu.table" -> "test_table")).mode("append").save()

  // Check for the existence of a Kudu table
  kuduContext.tableExists("another_table")

  // Delete a Kudu table
  kuduContext.deleteTable("unwanted_table")

  def getSparkSession(appName: String): SparkSession = {
    val warehouseLocation = "/spark/spark-warehouse"
    //    logger.info("set warehouse location：" + warehouseLocation)

    val conf = new SparkConf().setAppName(appName)
    conf.set("spark.sql.warehouse.dir", warehouseLocation)
    //
    //    if (EnvUtil.isLocal()) {
    //      logger.info("set to local mode")
    //      conf.setMaster("local[4]")
    //    }

    //es
    conf.set("pushdown", "true")
    conf.set("es.nodes.wan.only", "true")
    conf.set("es.mapping.date.rich", "false")
    conf.set("es.http.timeout", "2m")
    conf.set("es.index.read.missing.as.empty", "true")
    conf.set("es.input.max.docs.per.partition", "200000")
    conf.set("es.spark.dataframe.write.null", "true")
    conf.set("spark.debug.maxToStringFields", "1000000000")
    conf.set("spark.sql.caseSensitive", "true")
    //es

    val spark = SparkSession
      .builder()
      .config(conf)
      .enableHiveSupport()
      .getOrCreate()

    spark.sqlContext.setConf("spark.sql.hive.convertMetastoreParquet", "false")
    spark.sqlContext.setConf("spark.sql.parquet.compression.codec", "snappy")
    spark.sqlContext.setConf("hive.exec.dynamic.partition", "true")
    spark.sqlContext.setConf("hive.exec.dynamic.partition.mode", "nonstrict")

    spark.sqlContext.setConf("spark.storage.memoryFraction", "4")
    spark.sqlContext.setConf("spark.driver.cores", "4")

    spark.sqlContext.setConf("spark.sql.broadcastTimeout", "3000")


    spark
  }

}

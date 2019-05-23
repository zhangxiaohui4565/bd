package com.gupao.bigdata.spark.demo

object HelloWorld {

  def test(i: Int) = {
    if (i < 0) {
      "a"
    } else {
      1
    }
  }

  def main(args: Array[String]): Unit = {
    val f0 : (Int, Int) => Boolean = (i, j) =>  {i % 2 == 0}
    1.to(10)
  }

}

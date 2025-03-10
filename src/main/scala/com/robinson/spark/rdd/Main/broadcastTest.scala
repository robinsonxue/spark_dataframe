package com.robinson.spark.rdd.Main

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/**
  * Created by admin on 2017/9/28.
  */
object broadcastTest {
  def main(args: Array[String]) {

    val spark = SparkSession
      .builder()
      .master("spark://node01:9000")
      .appName("Multi-Broadcast Test")
      .getOrCreate()

    val slices = if (args.length > 0) args(0).toInt else 2
    val num = if (args.length > 1) args(1).toInt else 1000000

    val arr1 = new Array[Int](num)
    for (i <- 0 until arr1.length) {
      arr1(i) = i
    }

    val arr2 = new Array[Int](num)
    for (i <- 0 until arr2.length) {
      arr2(i) = i
    }

    val barr1 = spark.sparkContext.broadcast(arr1)
    val barr2 = spark.sparkContext.broadcast(arr2)
    val observedSizes: RDD[(Int, Int)] = spark.sparkContext.parallelize(1 to 10, slices).map { _ =>
      (barr1.value.length, barr2.value.length)
    }
    // Collect the small RDD so we can print the observed sizes locally.
    observedSizes.collect().foreach(i => println(i))

    spark.stop()
  }
}

package edu.knoldus

import org.scalatest.FunSuite

import scala.concurrent.Await
import scala.concurrent.duration._

class TweetsTest extends FunSuite{
 val tweets= new Tweets
  test("getting number of edu.knoldus.Tweets"){
    assert(Await.result(tweets.countTweets("#anmol"),10.seconds)==31)
  }
  test("Number of likes and retweets"){
    assert(Await.result(tweets.getAverageLikesAndRetweets("#anmol"),10.seconds).nonEmpty)
  }

  test("likes and retweets"){
    assert(tweets.getLikesAndReTweets("#anmol").nonEmpty)
  }
}

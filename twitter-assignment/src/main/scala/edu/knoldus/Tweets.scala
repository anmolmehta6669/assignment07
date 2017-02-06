package edu.knoldus

import java.io.File

import com.typesafe.config.ConfigFactory
import twitter4j._
import twitter4j.conf.ConfigurationBuilder

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Tweets{

  val parsedConfig = ConfigFactory.parseFile(new File(".\\src\\main\\resources\\application.conf"))
  val conf = ConfigFactory.load(parsedConfig)
  val log: Logger = Logger.getLogger(this.getClass)
  def getTweets(hashtag:String): List[Status] = {
    log.info("Initializing Config")
    val consumerKey = conf.getString("consumerKey")
    val consumerSecretKey = conf.getString("consumerSecretKey")
    val accessToken = conf.getString("accessToken")
    val accessTokenSecret = conf.getString("accessTokenSecret")
    val confBuilder = new ConfigurationBuilder()
    confBuilder.setDebugEnabled(false)
      .setOAuthConsumerKey(consumerKey)
      .setOAuthConsumerSecret(consumerSecretKey)
      .setOAuthAccessToken(accessToken)
      .setOAuthAccessTokenSecret(accessTokenSecret)
    val twitter: Twitter = new TwitterFactory(confBuilder.build()).getInstance()
    val query = new Query(hashtag)
    val maxTweet=100
    query.setCount(maxTweet)
    val list = twitter.search(query)
    list.getTweets.asScala.toList
  }

  def retrieveTweets(hashtag:String):List[MyTweets]={
    log.info("Retreiving edu.knoldus.Tweets")
    val tweets=getTweets(hashtag)
    val allTweets = tweets.map {
      tweet =>
        MyTweets(tweet.getText, tweet.getUser.getScreenName, tweet.getCreatedAt)
    }
    allTweets.sortBy(_.date)
  }

  def getLikesAndReTweets(hashtag:String):List[LikesAndRetweets]= {
    log.info("")
    val tweets=getTweets(hashtag)
     tweets.map {
      tweet => LikesAndRetweets(tweet.getFavoriteCount, tweet.getRetweetCount)
    }
  }

  def getAverageLikesAndRetweets(hashtag:String):Future[List[LikesAndRetweets]]={
   Future {
     val listOfLikesAndRetweets = getLikesAndReTweets(hashtag)
     val totalLikes = listOfLikesAndRetweets.map(x => x.likes).sum
     val totalRetweets = listOfLikesAndRetweets.map(x => x.retweets).sum
     listOfLikesAndRetweets.map {
       list => LikesAndRetweets(list.likes / totalLikes, list.retweets / totalRetweets)
     }
   }
  }

  def countTweets(hashtag:String):Future[Double]={
    Future {retrieveTweets(hashtag).size}
  }

/**
  * Function to be defined for average tweets per day
  def averageTweetsPerDay(hashtag:String):Map[Int,Double]={
    val averageTweets=retrieveTweets(hashtag).groupBy(tweets=>tweets.date.)
    averageTweets.map(day=>(day._1,day._2.size/countTweets(hashtag)))
  }
  */

}

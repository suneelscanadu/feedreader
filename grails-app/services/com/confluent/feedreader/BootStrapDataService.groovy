package com.confluent.feedreader

//import grails.transaction.Transactional
import org.slf4j.Logger;
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional


/**
 * 
 * @author suneel
 *
 * Load the seed data for articles, users and feeds during the application startup
 * 
 * The articles are numbered from 1 to 100
 * The users are numbered from 1 to 100
 * The feeds are numbered from 1 to 100
 * 
 * The data is stored in respective concurrentMaps just in case if they are modified and to avoid concurrency issues
 * 
 */
@Transactional
class BootStrapDataService {
	
	def feedService
	static Logger log = LoggerFactory.getLogger(BootStrapDataService.class)
	private final ConcurrentMap<Long, Feed> feeds = new ConcurrentHashMap<Long, Feed>()
	private final ConcurrentMap<Long, User> users = new ConcurrentHashMap<Long, User>()
	private final ConcurrentMap<Long, Article> articles = new ConcurrentHashMap<Long, Article>()
	
	/*
	 * create users at application startup from 1-1oo
	 */
    def createUsers() {
		for(long i=1; i<= 100; i++) {
			User user = new User()
			user.email= "user_" + i + ".mail.com"
			user.userId = i
			users.put(i, user)
		}
		log.info("suncessfully bootstrap 1-100 users")
	}
	
	/*
	 * create feeds at application startup from 1-100 
	 */
	def createFeeds() {
		for(long i=1; i<= 100; i++) {
			Feed feed = new Feed()
			feed.feedId = i
			feed.name = "feed_" + i
			feed.description = "This is feed# " + i
			feeds.put(i, feed)
		}
		log.info("suncessfully bootstrap 1-100 feeds")
	}
	
	/*
	 * create articles at application startup from 1-100
	 */
	def createArticles() {
		for(long i=1; i<= 100; i++) {
			Article article = new Article()
			article.articleId = i
			article.name = "article_" + i
			article.description = "This is article# " + i
			
			articles.put(i, article)
			
		}
		log.info("suncessfully bootstrap 1-100 articles")
	}
	
	/*
	 * supports user search in the memory
	 */
	public User getUser(Long userId) {
		return users.get(userId)
	}
	
	/*
	 * supports feed search in the memory
	 */
	
	public Feed getFeed(Long feedId) {
		return feeds.get(feedId)
	}
	
	/*
	 * supports article search in the memory
	 */
	public Article getArticle(Long articleId) {
		return articles.get(articleId)
	}
}

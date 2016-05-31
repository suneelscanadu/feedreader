package com.confluent.feedreader

import grails.converters.JSON

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author: suneel
 * User can subscribe and unsubscribe to feed 
 * All the articles, feeds and users are pre loaded during the application start up
 * 
 */

class UserController {

	def bootStrapDataService
	def userService
	def feedService
	static Logger log = LoggerFactory.getLogger(UserController.class)
	static responseFormats = ['json'];
	
	/*
	 * User subscription to a feed
	 * The articles, feeds and users are already bootstrap loaded at the start up of the application
	 * 
	 */
    def subscribeFeed() {
		def req
		try {
			req = request.JSON;
		}
		catch(org.grails.web.converters.exceptions.ConverterException e) {
			def error=[:]
			error["message"] = "Invalid payload."
			response.status = 400
			render (error as JSON)
			
			return
		}
		
		UserFeed userFeed = new UserFeed()
		Long userId = req.userId
		Long feedId = req.feedId

		userFeed.user = bootStrapDataService.getUser(userId)
		if(userFeed.user == null ) {
			def error=[:]
			error["message"] = "User not found"
			response.status = 400
			render (error as JSON)
			
			return
		}
		
		userFeed.feed = bootStrapDataService.getFeed(feedId)
		if(userFeed.feed == null ) {
			def error=[:]
			error["message"] = "Feed not found"
			response.status = 400
			render (error as JSON)
			
			return
		}
		
		userFeed.operation = "SUBSCRIBE"
		
		userService.subscribeFeed(userFeed)
		
		List<UserFeed> lstUserFeed = userService.getUserFeedList(userId)
		response.status = 200
		render (lstUserFeed as JSON)
	}
	
	/*
	 * gets all subscriptions for an user in JSON
	 */
	def getUserFeeds() {
		String userId = Long.parseLong(params.userId.toString())
		if(userId == null || userId.isEmpty()) {
			def error=[:]
			error["message"] = "userId is missing in the Query String."
			response.status = 400
			render (error as JSON)
			
			return
		}
		List<UserFeed> lstUserFeed = userService.getUserFeedList(userId)
		response.status = 200
		render (lstUserFeed as JSON)
	}
	
	/*
	 * User unsubscription to a feed
	 * The articles, feeds and users are already bootstrap loaded at the start up of the application
	 * 
	 */
	def unsubscribeFeed() {
		def req 
		
		try {
			req = request.JSON;
		}
		catch(org.grails.web.converters.exceptions.ConverterException e) {
			def error=[:]
			error["message"] = "Invalid payload."
			response.status = 400
			render (error as JSON)
			
			return
		}
		
		Long userId = req.userId
		Long feedId = req.feedId
		
		UserFeed userFeed = new UserFeed()
		userFeed.user = bootStrapDataService.getUser(userId)
		if(userFeed.user == null ) {
			def error=[:]
			error["message"] = "User not found"
			response.status = 400
			render (error as JSON)
			
			return
		}
		
		userFeed.feed = bootStrapDataService.getFeed(feedId)
		if(userFeed.feed == null ) {
			def error=[:]
			error["message"] = "Feed not found"
			response.status = 400
			render (error as JSON)
			
			return
		}
		
		userFeed.operation = "UNSUBSCRIBE"
		
		userService.unSubscribeFeed(userFeed)
		
		List<UserFeed> lstUserFeed = userService.getUserFeedList(userId)
		response.status = 200
		if(lstUserFeed != null) {
			render (lstUserFeed as JSON)
		}
		else {
			render [:] as JSON
		}
		
		
	}
	
	/*
	 * Get all the articles for each feed for the feeds subscribed by user
	 * TBD: Pagination
	 * 
	 */
	def userArticles() {
		Long userId = Long.parseLong(params.userId)
		
		List<UserFeed> userfeeds = userService.getUserFeedList(userId)
		List userFeedArticles = new ArrayList()
		for(UserFeed userFeed : userfeeds ) {
			def feedInfo= [:]
			
			Long feedId = userFeed.Feed.feedId
			List<FeedArticle> lstFeedArticles = feedService.getFeedArticlesList(feedId)
			List<Article> articles = new ArrayList<Article>()
			
			Iterator<FeedArticle> articleIterator = lstFeedArticles.iterator()
			while (articleIterator.hasNext()) {
				FeedArticle feedArticle = articleIterator.next()
				def articleInfo =[:]
				articleInfo["articleId"] = feedArticle.article.articleId
				articleInfo["name"] = feedArticle.article.name
				articleInfo["description"] = feedArticle.article.description
				articles.add(articleInfo)
			}
			
			feedInfo["feedId"]=feedId
			feedInfo["articles"]=articles
			
			userFeedArticles.add(feedInfo)
		}
		
		def userFeedArticlesResp = [:]
		userFeedArticlesResp["userId"] = userId
		userFeedArticlesResp["data"]=userFeedArticles
		render (userFeedArticlesResp as JSON)
		
	}
	
}

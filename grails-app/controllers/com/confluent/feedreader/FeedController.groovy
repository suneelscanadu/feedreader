package com.confluent.feedreader

import grails.converters.JSON

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author suneel
 * Articles can be added and displayed base don the feed
 * All the articles, feeds and users are pre loaded during the application start up
 * 
 */
class FeedController {

	def bootStrapDataService
	def feedService
	static Logger log = LoggerFactory.getLogger(FeedController.class)
	static responseFormats = ['json']
	
	/*
	 * add articles to a feed
	 * All the articles, feeds and users are pre loaded during the application start up
	 */
    def addArticlesToFeed() {
		def req
		try {
			req = request.JSON;
		}
		catch(org.grails.web.converters.exceptions.ConverterException e) {
			log.error(e)
			def error=[:]
			error["message"] = "Invalid payload."
			response.status = 400
			render (error as JSON)
			
			return
		}
		
		Long feedId = req.feedId
		Long articleId = req.articleId
		FeedArticle feedArticle = new FeedArticle()
		
		feedArticle.feed = bootStrapDataService.getFeed(feedId)
		if(feedArticle.feed == null) {
			log.error("feed  ${feedId} not found")
			def error=[:]
			error["message"] = "feed not found" // all the feeds are preloaded during application startup
			response.status = 400
			render (error as JSON)
			
			return
		}
		
		feedArticle.article = bootStrapDataService.getArticle(articleId)
		if(feedArticle.article == null) {
			def error=[:]
			error["message"] = "article not found" // all the articles are preloaded during application startup
			response.status = 400
			render (error as JSON)
			
			return
		}
		
		feedArticle.operation = "ADD"
		
		feedService.addArticleToFeed(feedArticle)
		
		List<FeedArticle> lstFeedArticles = feedService.getFeedArticlesList(feedId)
		response.status = 200
		render (lstFeedArticles as JSON)
	}
	
	/*
	 * get all articles for a feed
	 * TBD: Pagination
	 */
	def getArticlesForFeed() {
		
		String strFeedId = params.feedId
		if(strFeedId==null) {
			def error=[:]
			error["message"] = "feedId cannot be null" 
			response.status = 400
			render (error as JSON)
			
			return
		}

		Long feedId = Long.parseLong(strFeedId)
		
		List<FeedArticle> lstFeedArticles = feedService.getFeedArticlesList(feedId)
		response.status = 200
		if(lstFeedArticles != null) {
			render (lstFeedArticles as JSON)
		}
		else {
			render [:] as JSON
		}
		
	}
}

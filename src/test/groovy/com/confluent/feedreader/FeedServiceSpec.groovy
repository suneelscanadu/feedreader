package com.confluent.feedreader

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(FeedService)
class FeedServiceSpec extends Specification {

	def bootStrapDataService
	def feedService
	
	def setup() {
		bootStrapDataService = new BootStrapDataService()
		feedService = new FeedService()
	}
	
    

    def cleanup() {
    }

    void "test adding an article11 to a feed"() {
		when:
			bootStrapDataService.createArticles()
			bootStrapDataService.createFeeds()
			
			def article = bootStrapDataService.getArticle(25)
			def feed = bootStrapDataService.getFeed(30)
			
			def feedArticle = new FeedArticle()
			feedArticle.setArticle(article)
			feedArticle.setFeed(feed)
			feedArticle.setOperation("ADD")
			feedService.addArticleToFeed(feedArticle)
			
		then:
		List<FeedArticle> lstFeedArticles = feedService.getFeedArticlesList(30)
		assert lstFeedArticles.contains(feedArticle)
		
    }
}

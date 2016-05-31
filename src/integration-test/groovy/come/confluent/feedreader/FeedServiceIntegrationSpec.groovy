package come.confluent.feedreader


import com.confluent.feedreader.BootStrapDataService
import com.confluent.feedreader.FeedArticle
import com.confluent.feedreader.FeedService
import grails.test.mixin.integration.Integration
import grails.transaction.*
import spock.lang.*

@Integration

class FeedServiceIntegrationSpec extends Specification {
	def bootStrapDataService
	def feedService
	
    def setup() {
		bootStrapDataService = new BootStrapDataService()
		feedService = new FeedService()
    }

    def cleanup() {
    }

    void "test adding an article to a feed"() {
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
	
	void "test adding already added article to a feed"() {
		when:
			bootStrapDataService.createArticles()
			bootStrapDataService.createFeeds()
			
			def article = bootStrapDataService.getArticle(25)
			def feed = bootStrapDataService.getFeed(30)
			
			def feedArticle = new FeedArticle()
			feedArticle.setArticle(article)
			feedArticle.setFeed(feed)
			feedArticle.setOperation("ADD")
			int resp1 = feedService.addArticleToFeed(feedArticle)
			int resp2 = feedService.addArticleToFeed(feedArticle)
			
		then:
			resp2==0
		
	}
	
	
}

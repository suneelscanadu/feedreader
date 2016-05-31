package come.confluent.feedreader


import com.confluent.feedreader.*
import grails.test.mixin.TestFor
import grails.test.mixin.integration.Integration
import grails.transaction.*
import groovy.json.JsonSlurper
import spock.lang.*

@Integration
@TestFor(FeedController)
class FeedControllerIntegrationSpecSpec extends Specification {

	def bootStrapDataService
	def feedService
	
    def setup() {
		bootStrapDataService = new BootStrapDataService()
		feedService = new FeedService()
		bootStrapDataService.createArticles()
		bootStrapDataService.createUsers()
		bootStrapDataService.createFeeds()
    }

    def cleanup() {
    }

    void "test response when article is added to a feed"() {
		when:
			controller.bootStrapDataService = bootStrapDataService
			controller.feedService = feedService
			request.json = '{"articleId": 10, "feedId": 20}'
			request.method = 'POST'
			request.contentType = "application/json"
			controller.addArticlesToFeed()
		
		then:
			response.status==200
        
    }
	
	void "test article info when article is added to a feed"() {
		when:
			controller.bootStrapDataService = bootStrapDataService
			controller.feedService = feedService
			request.json = '{"articleId": 10, "feedId": 20}'
			request.method = 'POST'
			request.contentType = "application/json"
			controller.addArticlesToFeed()
			
			println response.json
		
		then:
			def list = new JsonSlurper().parseText( response.json.toString() )
			def tempArticleId= 0
			list.each { 
				if(it.article.articleId == 10)
					tempArticleId= it.article.articleId
			}
			tempArticleId ==10
		
	}
	void "test get articles for a given feed "() {
		when:
			controller.bootStrapDataService = bootStrapDataService
			controller.feedService = feedService
			params.feedId=20
			request.method = 'POST'
			request.contentType = "application/json"
			controller.getArticlesForFeed()
			
			println response.json
		
		then:
			response.status==200
		
	}
	void "test get articles for a given feed after adding aticle to a feed"() {
		when:
			controller.bootStrapDataService = bootStrapDataService
			controller.feedService = feedService
			request.json = '{"articleId": 99, "feedId": 45}'
			request.method = 'POST'
			request.contentType = "application/json"
			controller.addArticlesToFeed()
			
			println response.json
			
			params.feedId=45
			request.method = 'POST'
			request.contentType = "application/json"
			controller.getArticlesForFeed()
			
		
		then:
			def list = new JsonSlurper().parseText( response.json.toString() )
			def tempArticleId= 0
			list.each { 
				if(it.article.articleId == 99)
					tempArticleId= it.article.articleId
			}
			tempArticleId ==99
		
	}
	
	
	
	
	void "test for adding article to non existant feed"() {
		when:
			controller.bootStrapDataService = bootStrapDataService
			controller.feedService = feedService
			request.json = '{"articleId": 11, "feedId": 2000}'
			request.method = 'POST'
			request.contentType = "application/json"
			controller.addArticlesToFeed()
		
		then:
			response.json==["message":"feed not found"]
		
	}
	
	void "test for adding non existant article to a feed"() {
		when:
			controller.bootStrapDataService = bootStrapDataService
			controller.feedService = feedService
			request.json = '{"articleId": 1100, "feedId": 30}'
			request.method = 'POST'
			request.contentType = "application/json"
			controller.addArticlesToFeed()
		
		then:
			response.json==["message":"article not found"]
		
	}
}

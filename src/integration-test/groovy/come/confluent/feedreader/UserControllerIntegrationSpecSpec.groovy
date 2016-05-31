package come.confluent.feedreader


import com.confluent.feedreader.BootStrapDataService
import com.confluent.feedreader.UserController
import grails.test.mixin.TestFor
import com.confluent.feedreader.UserService
import grails.test.mixin.integration.Integration
import grails.transaction.*
import groovy.json.JsonSlurper
import org.grails.web.json.JSONObject
import spock.lang.*

@Integration
@TestFor(UserController)
class UserControllerIntegrationSpecSpec extends Specification {

	def bootStrapDataService
	def userService 
	//def userController
	
    def setup() {
		bootStrapDataService = new BootStrapDataService()
		userService = new UserService()
		bootStrapDataService.createArticles()
		bootStrapDataService.createUsers()
		bootStrapDataService.createFeeds()
		
		//userController = new UserController()
    }
	

    def cleanup() {
    }

    void "test user subscription to a feed"() {
		when:
			controller.bootStrapDataService = bootStrapDataService
			controller.userService = userService
			request.json = '{"userId": 1, "feedId": 2}'
			request.method = 'POST'
			request.contentType = "application/json"
			controller.subscribeFeed()
		
		then:
			response.status==200
        
    }
	
	void "test user subscription to a feed with non existent userId"() {
		when:
		controller.bootStrapDataService = bootStrapDataService
		controller.userService = userService
		
		request.json = '{"userId": 1000, "feedId": 2}'
		request.method = 'POST'
		request.contentType = "application/json"
		controller.subscribeFeed()
		
		then:
			response.json==["message":"User not found"]
		
	}
	
	void "test user subscription to a feed with non existent feedId"() {
		when:
			controller.bootStrapDataService = bootStrapDataService
			controller.userService = userService
			request.json = '{"userId": 10, "feedId": 2000}'
			request.method = 'POST'
			request.contentType = "application/json"
			controller.subscribeFeed()
		
		then:
			response.json==["message":"Feed not found"]
		
	}
	
	void "test user subscription to a feed and verify feed info"() {
		when:
			controller.bootStrapDataService = bootStrapDataService
			controller.userService = userService
			request.json = '{"userId": 100, "feedId": 2}'
			request.method = 'POST'
			request.contentType = "application/json"
			controller.subscribeFeed()
			
		then:
			def list = new JsonSlurper().parseText( response.json.toString() )
			def tempFeedId= 0
			list.each { 
				if(it.feed.feedId == 2)
					tempFeedId= it.feed.feedId
			}
			tempFeedId ==2
	}
	
	void "test user unsubscription to a feed and verify feed info"() {
		when:
			controller.bootStrapDataService = bootStrapDataService
			controller.userService = userService
			
			request.json = '{"userId": 100, "feedId": 2}'
			request.method = 'POST'
			request.contentType = "application/json"
			controller.unsubscribeFeed()
			
		then:
			def list = new JsonSlurper().parseText( response.json.toString() )
			if(list == null) {
				response.json ==[:]
			}
			else {
			
				def tempFeedId= 0
				list.each {
					if(it.feed.feedId == 2)
						tempFeedId= it.feed.feedId
				}
				tempFeedId ==0 
			}
	}
	
}

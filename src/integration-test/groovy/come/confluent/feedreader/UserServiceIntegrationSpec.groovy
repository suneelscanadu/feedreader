package come.confluent.feedreader


import com.confluent.feedreader.BootStrapDataService
import com.confluent.feedreader.UserFeed
import com.confluent.feedreader.UserService
import grails.test.mixin.integration.Integration
import grails.transaction.*
import spock.lang.*

@Integration

class UserServiceIntegrationSpec extends Specification {

   def bootStrapDataService
	def userService
	
    def setup() {
		bootStrapDataService = new BootStrapDataService()
		userService = new UserService()
    } 

    def cleanup() {
    }

    void "test subscribe a feed by an user"() {
		when:
			bootStrapDataService.createUsers()
			bootStrapDataService.createFeeds()
			
			def user = bootStrapDataService.getUser(23)
			def feed = bootStrapDataService.getFeed(54)
			
			def userFeed = new UserFeed()
			userFeed.setUser(user)
			userFeed.setFeed(feed)
			userFeed.setOperation("SUBSCRIBE")
			int status = userService.subscribeFeed(userFeed)
			
		then:
			List<UserFeed> lstUserFeeds = userService.getUserFeedList(23)
			assert lstUserFeeds.contains(userFeed)
		
    }
	
	void "test subscribing feed already subscribed for an user"() {
		when:
			bootStrapDataService.createUsers()
			bootStrapDataService.createFeeds()
			
			def user = bootStrapDataService.getUser(76)
			def feed = bootStrapDataService.getFeed(11)
			
			def userFeed = new UserFeed()
			userFeed.setUser(user)
			userFeed.setFeed(feed)
			userFeed.setOperation("SUBSCRIBE")
			int resp1 = userService.subscribeFeed(userFeed)
			int resp2 = userService.subscribeFeed(userFeed)
			
		then:
			resp2==0
		
	}
	
	void "test Unsubscribing a feed by an user "() {
		when:
			bootStrapDataService.createUsers()
			bootStrapDataService.createFeeds()
			
			def user = bootStrapDataService.getUser(43)
			def feed = bootStrapDataService.getFeed(84)
			
			def userFeed = new UserFeed()
			userFeed.setUser(user)
			userFeed.setFeed(feed)
			userFeed.setOperation("SUBSCRIBE")
			int status = userService.subscribeFeed(userFeed)
			
			userService.unSubscribeFeed(userFeed)
			
		then:
			List<UserFeed> lstUserFeeds = userService.getUserFeedList(43)
			assert lstUserFeeds==null || !lstUserFeeds.contains(userFeed)
		
	}
	
	
}

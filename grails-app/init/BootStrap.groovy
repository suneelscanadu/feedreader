import com.confluent.feedreader.BootStrapDataService
import com.confluent.feedreader.*


/**
 * 
 * @author suneel
 *
 * Executed during the application start up
 * all the articles, feeds and users are pre loaded in to the memory
 * 
 * The data related to user feeds and feed articles saved into file is loaded into memory
 * 
 * Separate threads are started to process user feeds and  feed articles and listen until interrupted
 * 
 */
class BootStrap {

	def bootStrapDataService
	def userService
	def feedService
	
    def init = { servletContext ->
		bootStrapDataService.createUsers()
		bootStrapDataService.createFeeds()
		bootStrapDataService.createArticles()
		
		feedService.loadFeedArticlesData()
		userService.loadUserFeedData()
		
		Thread t1 = new Thread(userService)
		t1.start()
		
		Thread t2 = new Thread(feedService)
		t2.start()
    }
    def destroy = {
    }
}

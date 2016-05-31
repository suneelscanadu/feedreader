package come.confluent.feedreader


import com.confluent.feedreader.BootStrapDataService
import grails.test.mixin.integration.Integration
import grails.transaction.*
import spock.lang.*

@Integration
class BootStrapDataServiceIntegrationSpecSpec extends Specification {

	def bootStrapDataService
    def setup() {
		bootStrapDataService = new BootStrapDataService()
    }

    def cleanup() {
    }

    void "test creating some default articles "() {
		when:
			bootStrapDataService.createArticles()
		then:
			bootStrapDataService.getArticle(1).name == "article_1"
    }
	
	void "test creating some default users "() {
		when:
			bootStrapDataService.createUsers()
		then:
			bootStrapDataService.getUser(1).userId == 1
	}
	
	void "test creating some default feeds "() {
		when:
			bootStrapDataService.createFeeds()
		then:
			bootStrapDataService.getFeed(1).name == "feed_1"
	}
}

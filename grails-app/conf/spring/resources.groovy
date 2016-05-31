// Place your Spring DSL code here
import grails.rest.render.json.JsonRenderer

import com.confluent.feedreader.*
beans = {
	UserRenderer(JsonRenderer, User) {
		includes = ['userId','email']
	}
	FeedRenderer(JsonRenderer, Feed) {
		includes = ['feedId', 'name', 'description']
	}
	
	ArticleRenderer(JsonRenderer, Article) {
		includes = ['articleId', 'name', 'description']
	}
	
	UserFeedRenderer(JsonRenderer, UserFeed) {
		includes = ['feed']
	}
	
	FeedArticleRenderer(JsonRenderer, FeedArticle) {
		includes = ['article']
	}
}

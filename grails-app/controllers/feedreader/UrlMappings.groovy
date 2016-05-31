package feedreader

class UrlMappings {

	
    static mappings = {
		
		"/userfeed/v1/subscribe"(controller: "user", action: "subscribeFeed", method: "POST")
		"/userfeed/v1/unsubscribe"(controller: "user", action: "unsubscribeFeed", method: "POST")
		"/userfeed/v1/article"(controller: "feed", action: "addArticlesToFeed", method: "POST")
		"/userfeed/v1/articles?"(controller: "feed", action: "getArticlesForFeed", method: "GET")
		"/userfeed/v1/user/articles?"(controller: "user", action: "userArticles", method: "GET")
        
		"/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}

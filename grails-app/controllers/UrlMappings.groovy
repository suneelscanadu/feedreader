class UrlMappings {

    static mappings = {
		
		"/userfeed/subscribe"(controller: "user", parseRequest: true) {
			action = [ POST: "subscribeFeed", GET: "unsupported", PUT: "unsupported", DELETE: "unsupported"]
			format:"json"
		}
		
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}

package com.confluent.feedreader

class UserFeed {

    static constraints = {
    }
	
	User user
	Feed Feed
	String operation

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Feed == null) ? 0 : Feed.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserFeed other = (UserFeed) obj;
		if (Feed == null) {
			if (other.Feed != null)
				return false;
		} else if (!Feed.equals(other.Feed))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}}

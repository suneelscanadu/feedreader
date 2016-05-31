package com.confluent.feedreader

class Feed {

    static constraints = {
		
    }
	
	Long feedId
	String name
	String description
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((feedId == null) ? 0 : feedId.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Feed other = (Feed) obj;
		if (feedId == null) {
			if (other.feedId != null)
				return false;
		} else if (!feedId.equals(other.feedId))
			return false;
		return true;
	}	
	
}

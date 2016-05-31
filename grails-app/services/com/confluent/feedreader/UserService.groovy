package com.confluent.feedreader

import java.text.SimpleDateFormat
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * 
 * @author suneel
 * 
 * serves the tasks related to feeds and users
 * The data is stored in the concurrentMap to support concurrency
 * The events are logged into a blocking queue from where a separate thread writes the events to a file
 *  while the data is in the memory in concurrentMap
 *  
 *  The data from the file is loaded at application startup and available in the memory in concurrentHashmap
 *  
 *  The data files are partitioned based on userId
 */

class UserService implements Runnable{

	def bootStrapDataService
	static Logger log = LoggerFactory.getLogger(UserService.class)
	private final ConcurrentMap<Long, List<UserFeed>> userFeedData = new ConcurrentHashMap<Long, List<UserFeed>>()
	private final BlockingQueue<UserFeed> userFeedQueue = new ArrayBlockingQueue<UserFeed>(10);
	
	/*
	 * takes the item from the blocking queue and writes to file
	 */
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				UserFeed  userFeed = userFeedQueue.take()
				writeToFile(userFeed)
			}
		}
		catch(InterruptedException e) {
			//e.printStackTrace()
			log.error(e)
		}
	}
	
	/*
	 * The data is saved in the memory in concurrentMap 
	 * and also in the Blocking Queue from where a separate thread writes the data to a file
	 * 
	 */
	public int subscribeFeed(UserFeed userFeed) {
		try {
			List<UserFeed> temp = new ArrayList<UserFeed>()
			temp.add(userFeed)
			List<UserFeed> userFeeds = userFeedData.putIfAbsent(userFeed.user.userId, temp)
			
			if(userFeeds != null) {
				if(!userFeeds.contains(userFeed)) {
					userFeeds.add(userFeed)
					userFeedData.put(userFeed.user.userId, userFeeds)
					userFeedQueue.put(userFeed)
					
					return 1
				}
				else {
					log.error( "User already subscribed to feed:${userFeed.feed.feedId}")
					
					return 0
				}
			}
			else {
				//in the case if key is not found
				userFeedQueue.put(userFeed)
				return 1
			}
			
		}
		catch(InterruptedException e) {
			log.error(e)
			Thread.currentThread().interrupt()
			throw new RuntimeException("Unexpected interruption");
			return 0
		}
	}
	
	/*
	 * Removes the feed from the user
	 * The data is removed from the memory in conurrentMap for a given user and feed
	 * Also writes the even to the blocking queue with the operation set to UNSUBSCRIBE so that a separate thread writes to a file
	 * and the data is restored at the application startup
	 *  
	 */
	public boolean unSubscribeFeed(UserFeed userFeed) {
		List<UserFeed> lstUserFeed = userFeedData.get(userFeed.user.userId)
		if(lstUserFeed==null || lstUserFeed.size() ==0) {
			return false
		}
		else {
			if(lstUserFeed.remove(userFeed)) {
				userFeedQueue.put(userFeed)
				
				return true
			}
			return false
			
		}
	}
	
	/*
	 * get all the feeds of an user
	 */
	public List<UserFeed> getUserFeedList(Long userId) {
		return userFeedData.get(userId)
	}
	
	/*
	 * load the feeds of all the users one time during application startup and the data is made available in the memory in concurrentMap
	 * 
	 */
	public loadUserFeedData() {
		File dir = new File("data" + File.separator + "userfeeds" + File.separator)
		File[] directoryListing = dir.listFiles()
		
		for(File fileName: directoryListing) {
			
			if(fileName.isHidden() ) {
				continue
			}
			
			BufferedReader br=null
			try {
				br = new BufferedReader(new FileReader(fileName))
				List<UserFeed> userfeeds = new ArrayList<UserFeed>()
				String userId = ""
				String line = br.readLine()
				while(line  != null) {
					
					String[] tokens = line.split("\\|")
					userId = tokens[2]
					String feedId = tokens[3]
					User user = bootStrapDataService.getUser(Long.parseLong(userId))
					Feed feed = bootStrapDataService.getFeed(Long.parseLong(feedId))
					UserFeed userFeed = new UserFeed()
					userFeed.user=user
					userFeed.feed=feed
					
					if(tokens[1].equals("SUBSCRIBE")) {
						
						userFeed.operation = "SUBSCRIBE"
						
						userfeeds.add(userFeed)
						
					}
					else if(tokens[1].equals("UNSUBSCRIBE")){
						userfeeds.remove(userFeed)
					}
					line = br.readLine()
				}
				
				userFeedData.put(Long.parseLong(userId), userfeeds)
				
				log.info("suncessfully bootstrap loaded userfeeds")
			}
			catch (FileNotFoundException e) {
				log.error(e)
			}
			catch (IOException e) {
				log.error(e)
			}
			finally {
				br.close()
			}
		}
	}
	
	/*
	 * writes the data to a file on the disk and this file is used to load the data into memory during application startup
	 * * The data for each user is stored in a separator file 
	 * so that there is no blocking while writing to  file and also serves as data partition
	 */
	private void writeToFile(UserFeed userFeed) {
		BufferedWriter bw = null
		PrintWriter out = null
		
		try{
			File file = new File("data" + File.separator + "userfeeds" + File.separator + "userfeed_${userFeed.user.userId}_data.txt")
			file.getParentFile().mkdirs()
			FileWriter fw = new FileWriter(file, true)
			bw = new BufferedWriter(fw)
			out = new PrintWriter(bw)
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
			out.println(sdf.format(new Date()) + "|" + userFeed.operation + "|" + userFeed.user.userId + "|" + userFeed.feed.feedId);
			out.flush()
		}
		catch(IOException e) {
			log.error(e)
		}
		catch(Exception e) {
			log.error(e)
		}
		finally{
			out.close()
		}
	}
}

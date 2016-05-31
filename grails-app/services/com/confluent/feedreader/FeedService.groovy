package com.confluent.feedreader

import java.text.SimpleDateFormat
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
/**
 *
 * @author suneel
 * 
 * serves the tasks related to feeds and articles
 * The data is stored in the concurrentMap to support concurrency
 * The events are logged into a blocking queue from where a separate thread writes the events to a file
 *  while the data is in the memory in concurrentMap
 *
 *  The data from the file is loaded at application startup and available in the memory in concurrentHashmap
 *  
 *  The data files are partitioned based on userId
 */
@Transactional
class FeedService implements Runnable {

	def fileUtilsService
	def bootStrapDataService
    private final ConcurrentMap<Long, List<FeedArticle>> feedArticlesData = new ConcurrentHashMap<Long, List<FeedArticle>>()
	private final BlockingQueue<FeedArticle> feedArticlesQueue = new ArrayBlockingQueue<FeedArticle>(100);
	static Logger log = LoggerFactory.getLogger(FeedService.class)
	/*
	 * takes the feed article item from the blocking queue and writes to file
	 */
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				FeedArticle  feedArticle = feedArticlesQueue.take()
				writeToFile(feedArticle)
			}
		}
		catch(InterruptedException e) {
			log.error(e)
		}
	}
	
	/*
	 * The data is saved in the memory in concurrentMap 
	 * and also in the Blocking Queue from where a separate thread writes the data to a file
	 */
	public int addArticleToFeed(FeedArticle feedArticle) {
		List<FeedArticle> lst = new ArrayList<FeedArticle>()
		lst.add(feedArticle)
		List<FeedArticle> lstFeedArticle = feedArticlesData.putIfAbsent(feedArticle.feed.feedId, lst)
		if(lstFeedArticle != null) {
			if(!lstFeedArticle.contains(feedArticle)) {
				lstFeedArticle.add(feedArticle)
				feedArticlesData.put(feedArticle.feed.feedId, lstFeedArticle)
				feedArticlesQueue.put(feedArticle)
				return 1
			}
			else {
				return 0
				log.error("article already available to feed")
			}
		}
		
		else {
			feedArticlesQueue.put(feedArticle)
			return 1
		}
	}
	
	/*
	 * get all the articles for a feed
	 */
	public List<FeedArticle> getFeedArticlesList(Long feedId) {
		return feedArticlesData.get(feedId)
	}
	
	/*
	 * load the articles of all the feeds one time during application startup 
	 * and the data is made available in the memory in concurrentMap
	 */
	public void loadFeedArticlesData() {
		File dir = new File("data" + File.separator + "feedarticles" + File.separator)
		File[] directoryListing = dir.listFiles()
		
		for(File fileName: directoryListing) {
			if(fileName.isHidden() ) {
				continue
			}
			BufferedReader br=null
			try {
				br = new BufferedReader(new FileReader(fileName))
				List<FeedArticle> feedArticles = new ArrayList<FeedArticle>()
				String feedId = ""
				String line = br.readLine()
				while(line  != null) {
					String[] tokens = line.split("\\|")
					if(tokens[1].equals("ADD")) {
						feedId = tokens[2]
						String articleId = tokens[3]
						Article article = bootStrapDataService.getArticle(Long.parseLong(articleId))
						Feed feed = bootStrapDataService.getFeed(Long.parseLong(feedId))
						FeedArticle feedArticle = new FeedArticle()
						feedArticle.article=article
						feedArticle.feed=feed
						feedArticle.operation = "ADD"
						
						feedArticles.add(feedArticle)
						
					}
					line = br.readLine()
				}
				
				feedArticlesData.put(Long.parseLong(feedId), feedArticles)
				log.info("suncessfully bootstrap loaded feed articles")
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
	 * The data for each feed is stored in a separator file 
	 * so that there is no blocking while writing to  file and also serves as data partition
	 */
	private void writeToFile(FeedArticle  feedArticleMessage) {
		BufferedWriter bw = null
		PrintWriter out = null
		try {
			File file = new File("data" + File.separator + "feedarticles" + File.separator + 
				"feed_articles_${feedArticleMessage.feed.feedId}_data.txt");
			file.getParentFile().mkdirs();
			FileWriter fw = new FileWriter(file, true);
			bw = new BufferedWriter(fw);
			out = new PrintWriter(bw)
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			out.println(sdf.format(new Date()) + "|" + feedArticleMessage.operation + "|" + feedArticleMessage.feed.feedId + "|" + feedArticleMessage.article.articleId);
			out.flush()
		} catch (IOException e) {
			//exception handling left as an exercise for the reader
			log.error(e)
		}
		finally {
			out.close()
		}
		
	}
}

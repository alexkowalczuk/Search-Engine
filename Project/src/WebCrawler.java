import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * @author alex
 *
 */
public class WebCrawler  {

	/**
	 * Default stemmer.
	 */
	private static SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * The Inverted Index to populate
	 */
	private final ThreadedInvertedIndex invertedIndex;

	/**
	 * Number of Threads
	 */
	private final int numThreads;

	/**
	 * The workQueue that will be used for multithreading.
	 */
	private WorkQueue workQueue;

	/**
	 * The number of maximum follows.
	 */
	private int limit;

	/**
	 * The set of links.
	 */
	private Set<URL> links;

	/**
	 * Constructor for the crawler class
	 *
	 * @param invertedIndex the index to use
	 * @param numThreads number of Threads
	 * @param limit maximum depth
	 */
	public WebCrawler (ThreadedInvertedIndex invertedIndex, int numThreads, int limit){
		this.invertedIndex = invertedIndex;
		this.numThreads = numThreads;
		this.limit = limit;
		this.links = new HashSet<URL>();
	}

	/**
	 * Stems the content of the cleaned html and adds it to the index.
	 *
	 * @param cleaned the cleaned HTML
	 * @param location the location string
	 * @param index the index to add to
	 * @throws IOException could happen
	 */
	public static void addStemmed(String cleaned, String location, InvertedIndex index) throws IOException {
		int position = 0;
		try (BufferedReader reader = new BufferedReader(new StringReader(cleaned));) {
			String line = null;
			SnowballStemmer stemmer = new SnowballStemmer(DEFAULT);
			while ((line = reader.readLine()) != null) {
				for (String word : TextParser.parser(line)) {
					position++;
					index.addEntry(stemmer.stem(word).toString(),location , position);
				}
			}
		}
	}

	/**
	 * This traverses the URLs and does all the hard work
	 * @param seed the seed url
	 * @throws IOException could happen
	 */
	public void traverse(URL seed) throws IOException {
		workQueue = new WorkQueue(numThreads);
		links.add(seed);
		workQueue.execute(new Task(seed));
		try {
			workQueue.finish();
		} catch (Exception e) {
			System.out.println("The work queue encountered an error.");
		}
		workQueue.shutdown();
	}

	/**
	 * A task class for multithreadig.
	 * @author alex
	 *
	 */
	private class Task implements Runnable {

		/**
		 * Given URL.
		 */
		private final URL url;

		/**
		 * Constructor for the Task.
		 * @param url the url to work on.
		 */
		public Task(URL url) {
			this.url = url;
		}

		@Override
		public void run() {
			HtmlCleaner htmlCleaner = new HtmlCleaner(this.url, HtmlFetcher.fetch(url, 3));
			try {
				if (HtmlFetcher.fetch(url, 3) == null) {
					return;
				}

				InvertedIndex local = new InvertedIndex();
				addStemmed(htmlCleaner.getHtml(), url.toString(), local);
				invertedIndex.addAll(local);

				synchronized(links) {
					for (URL url : htmlCleaner.getUrls()) {
						if (links.size() < limit && links.add(url)) {
							workQueue.execute(new Task(url));
						} else if (links.size() == limit) {
							break;
						}
					}
				}

			} catch (Exception e){
				System.out.println("Something went wrong while adding the cleaned HTML to the index.");
			}
		}
	}

}
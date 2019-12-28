import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Parse queries from file and perform search.
 * 
 * @author alex
 */
public class ThreadedQueryHandler implements QueueInterface {

	/**
	 * Protected caller for Inverted-Index
	 */
	private final ThreadedInvertedIndex invertedIndex;

	/**
	 * Protected number of Threads
	 */
	private final int numThreads;

	/**
	 * This is TreeMap to create QuerySet.
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.SearchResult>> querySet;

	/**
	 * This is our counstructor.
	 * 
	 * @param invertedIndex the index to process
	 * @param numThreads
	 */
	public ThreadedQueryHandler(ThreadedInvertedIndex invertedIndex, int numThreads) {
		this.invertedIndex = invertedIndex;
		this.querySet = new TreeMap<>();
		this.numThreads = numThreads;
	}

	/**
	 * This function get queries.
	 *
	 * @return set of Queries.
	 */
	@Override
	public Set<String> getQueries() {
		synchronized (querySet) {
			return Collections.unmodifiableSet(this.querySet.keySet());
		}
	}

	/**
	 * This function get the results associated to a specific query.
	 *
	 * @param queryLine The line that we are looking for.
	 * @return An unmodifiable List of Results.
	 */
	@Override
	public List<InvertedIndex.SearchResult> getResults(String queryLine) {
		synchronized (querySet) {
			return Collections.unmodifiableList(this.querySet.get(queryLine));
		}
	}

	/**
	 * This function writes the queries.
	 *
	 * @param outputFile Output file.
	 * @throws IOException Possible?
	 */
	@Override
	public void writeQuery(Path outputFile) throws IOException {
		synchronized (querySet) {
			SimpleJsonWriter.asQuery(this.querySet, outputFile);
		}
	}

	/**
	 * This function that checks if the map is empty.
	 * @return True if empty.
	 */
	@Override
	public boolean isEmpty() {
		synchronized (querySet) {
			return this.querySet.keySet().size() == 0;
		}
	}

	/**
	 * This function open query, clean and stem the queries, and put them to
	 * TreeSet.
	 * @param line to make
	 * @param exactSearch we processing
	 */
	@Override
	public void parseQuery(String line, boolean exactSearch) {
		TreeSet<String> queries = TextFileStemmer.uniqueStems(line);

		if (queries.isEmpty()) {
			return;
		}

		String joined = String.join(" ", queries);
		
		synchronized (querySet) {
			if (querySet.containsKey(joined)) {
				return;
			}
		}

		ArrayList<InvertedIndex.SearchResult> local = invertedIndex.search(queries, exactSearch);
		synchronized (querySet) {
			this.querySet.put(joined, local);
		}
	}

	/**
	 * This function open query, clean and stem the queries, and put them to
	 * TreeSet.
	 * 
	 * @param path path we process
	 * @param exactSearch we processing
	 * @throws IOException
	 */
	@Override
	public void parseQuery(Path path, boolean exactSearch) throws IOException {
		WorkQueue workQueue = new WorkQueue(numThreads);
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String query;
			while ((query = reader.readLine()) != null) {
				workQueue.execute(new Task(query, exactSearch));
			}
		}
		try {
			workQueue.finish();
		} catch (Exception e) {
			System.out.println("Sorry, something went wrong with parsing the query");
		}

		workQueue.shutdown();
	}

	/**
	 * This is the inner runnable task class
	 * @author alex
	 */
	private class Task implements Runnable {

		/** The prime number to add or list. */
		private final String line;

		/**
		 * This decides if it's exact search or not
		 */
		private final boolean exact;

		/**
		 * @param line  line to parse param invertedIndex index to use
		 * @param exact
		 */
		public Task(String line, boolean exact) {
			this.line = line;
			this.exact = exact;
		}

		@Override
		public void run() {
			parseQuery(line, exact);
		}
	}
}

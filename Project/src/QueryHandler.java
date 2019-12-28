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
 *
 *This class is building search queries index objects.
 */
public class QueryHandler implements QueueInterface {

	/**
	 * This is inverted index caller.
	 */
	private final InvertedIndex invertedIndex;

	/**
	 * Function to set sorted queries.
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.SearchResult>> querySet;

	/**
	 * Construstor for QueryHandler
	 * @param invertedIndex invertedIndex
	 */
	public QueryHandler(InvertedIndex invertedIndex) { 
		this.invertedIndex = invertedIndex;
		this.querySet = new TreeMap<>();
	}
	
	/**
	 * This function is a unmodifiable getter for our querySet.
	 * @param queryLine string 
	 * @return unmodifiable list of queryset
	 */
	@Override
	public List<InvertedIndex.SearchResult> getResults(String queryLine) {
		return Collections.unmodifiableList(this.querySet.get(queryLine));
	}
	
	/**
	 * This function is getting unmodifiable set of queries
	 * @return unmodifiable set of queries
	 */
	@Override
	public Set<String> getQueries() {
		return Collections.unmodifiableSet(this.querySet.keySet());
	}
	
	/**
	 * This function writes query using JSON
	 * @param outputFile function writes
	 * @throws IOException
	 */
	@Override
	public void writeQuery(Path outputFile) throws IOException {
		SimpleJsonWriter.asQuery(this.querySet, outputFile);
	}
	
	/**
	 * This function check if is empty.
	 *
	 * @return true if empty.
	 */
	@Override
	public boolean isEmpty() {
		return this.querySet.keySet().size() == 0;
	}

	/**
	 * This function open query, clean and stem the queries, and put them to TreeSet.
	 * @param path path we process
	 * @param exactSearch we processing
	 * @throws IOException 
	 */
	@Override
	public void parseQuery(Path path, boolean exactSearch) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String query;

			while ((query = reader.readLine()) != null) {
				parseQuery(query, exactSearch);
			}
		}
	}

	/**
	 * This function open query, clean and stem the queries, and put them to TreeSet.
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
		
		if (querySet.containsKey(joined)) {
			return;
		}
		
		ArrayList<InvertedIndex.SearchResult> local = invertedIndex.search(queries, exactSearch);
		this.querySet.put(joined, local);
		}
}
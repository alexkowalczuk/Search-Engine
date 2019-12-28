import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * 
 * This is interface to extend QueryHandler.
 * @author alex
 *
 */

public interface QueueInterface {
	
	/**
	 * This function is getting unmodifiable set of queries
	 * @return unmodifiable set of queries
	 */
	public Set<String> getQueries();
	
	/**
	 * This function is a unmodifiable getter for our querySet.
	 * @param queryLine string 
	 * @return unmodifiable list of queryset
	 */
	public List<InvertedIndex.SearchResult> getResults(String queryLine);
	
	/**
	 * This function writes query using JSON
	 * @param outputFile function writes
	 * @throws IOException
	 */
	public void writeQuery(Path outputFile) throws IOException;
	
	/**
	 * This function check if is empty.
	 * @return true if its empty.
	 */
	public boolean isEmpty();
	
	/**
	 * This function open query, clean and stem the queries, and put them to TreeSet.
	 * @param path
	 * @param exactSearch
	 * @throws IOException
	 */
	public void parseQuery(Path path, boolean exactSearch) throws IOException;
	
	/**
	 * @param line
	 * @param exactSearch
	 */
	public void parseQuery(String line, boolean exactSearch);

}

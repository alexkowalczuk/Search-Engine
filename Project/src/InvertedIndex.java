import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This is inverted index data structure that maps words to a map to locate
 * where the word is in file.
 * 
 * @author Alex Kowalczuk
 * @class Software Developer 212 at University of San Francisco (Fall 2019)
 */
public class InvertedIndex {

	/**
	 * This declare TreeMap for my inverted index
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;

	/**
	 * This declare new TreeMap for my count function
	 */
	private final TreeMap<String, Integer> counts;

	/**
	 * This is constructor to the inverted index class.
	 */
	public InvertedIndex() {
		this.invertedIndex = new TreeMap<>();
		this.counts = new TreeMap<>();
	}

	/**
	 * This function adds the array of words.
	 * 
	 * @param other inverted index
	 */
	public void addAll(InvertedIndex other) {
		for (String key : other.invertedIndex.keySet()) {
			if (this.invertedIndex.containsKey(key) == false) {
				this.invertedIndex.put(key, other.invertedIndex.get(key));
			} else {
				for (String path : other.invertedIndex.get(key).keySet()) {
					if (this.invertedIndex.get(key).containsKey(path) == false) {
						this.invertedIndex.get(key).put(path, other.invertedIndex.get(key).get(path));
					} else {
						this.invertedIndex.get(key).get(path).addAll(other.invertedIndex.get(key).get(path));
					}
				}
			}
		}

		for (String path : other.counts.keySet()) {
			if (this.counts.containsKey(path) == false) {
				this.counts.put(path, other.counts.get(path));
			} else {
				this.counts.put(path, Math.max(this.counts.get(path), other.counts.get(path)));

			}
		}
	}

	/**
	 * This function adds the array of words at once with default start at position
	 * 1
	 * 
	 * @param path    path
	 * @param element array of elements to add
	 */
	public void addAll(String path, String[] element) {
		addAll(path, element, 1);
	}

	/**
	 * This function adds the array of words at once with provided start position.
	 * 
	 * @param path    path
	 * @param element array of elements to add
	 * @param start   starting position
	 */
	public void addAll(String path, String[] element, int start) {
		for (String word : element) {
			addEntry(word, path, start++);
		}
	}

	/**
	 * This function Add Entry asserts important files and make sure its postition
	 * is right.
	 * 
	 * @param element to add
	 * @param path path to put in
	 * @param position to check
	 * @return true if the data structure was modified as a result of add()
	 */
	public boolean addEntry(String element, String path, int position) {
		invertedIndex.putIfAbsent(element, new TreeMap<String, TreeSet<Integer>>());
		invertedIndex.get(element).putIfAbsent(path, new TreeSet<Integer>());
		boolean added = this.invertedIndex.get(element).get(path).add(position);
		this.counts.putIfAbsent(path, position);

		if (position > counts.get(path)) {
			this.counts.put(path, position);
		}
		return added;
	}

	/**
	 * This function return unmodifiable map of counts.
	 * 
	 * @return num of counts which is unmodifiable Map
	 */
	public Map<String, Integer> getCounts() {
		return Collections.unmodifiableMap(counts);
	}

	/**
	 * This function print invertedIndex in a Json format to the output file.
	 * 
	 * @param outFile that uses SimpleJsonWriter to print out our file
	 * @throws IOException
	 */
	public void printIndex(Path outFile) throws IOException {
		SimpleJsonWriter.asDoubleNested(invertedIndex, outFile);
	}

	/**
	 * This function check if the map contains the specific word.
	 * 
	 * @param word to look in
	 * @return true is is passed
	 */
	public boolean contains(String word) {
		return invertedIndex.containsKey(word);
	}

	/**
	 * This function check if the map has the specific word and if word contain
	 * path.
	 * 
	 * @param word to check if have it
	 * @param path to check if word has path
	 * @return true if has, false if not
	 */
	public boolean contains(String word, String path) {
		return contains(word) ? invertedIndex.get(word).containsKey(path) : false;
	}

	/**
	 * This function check if the map contains the specific word, path and index.
	 * 
	 * @param word  check if the map contains the word.
	 * @param path  check if the map contains the path.
	 * @param index check if the map contains the index.
	 * @return true/false
	 */
	public boolean contains(String word, String path, int index) {
		return contains(word, path) ? invertedIndex.get(word).get(path).contains(index) : false;
	}

	/**
	 * This function returns how many words is in inverted index.
	 * 
	 * @return size of inverted index.
	 */
	public int size() {
		return invertedIndex.size();
	}

	/**
	 * This function returns how many paths are found in word.
	 * 
	 * @param word to check
	 * @return amount of paths, return 0 if not found
	 */
	public int size(String word) {
		return contains(word) ? invertedIndex.get(word).size() : 0;
	}

	/**
	 * This function get word as unmodifiable set of words if null will return empty
	 * set.
	 * 
	 * @return unmodifiable set of words.
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(this.invertedIndex.keySet());
	}

	/**
	 * This function gets unmodifiable location of our inverted index.
	 * 
	 * @param word we check location.
	 * @return unmodifiable set of word locations.
	 */
	public Set<String> getLocations(String word) {
		if (this.invertedIndex.get(word) == null) {
			return Collections.emptySet();
		} else {
			return Collections.unmodifiableSet(this.invertedIndex.get(word).keySet());
		}
	}

	/**
	 * This function gets unmodifiable position of our inverted index if null will
	 * return empty set.
	 * 
	 * @param word     we use
	 * @param location we look
	 * @return unmodifiable set of position.
	 */
	public Set<Integer> getPositions(String word, String location) {
		if (this.invertedIndex.get(word).get(location) == null) {
			return Collections.emptySet();
		} else {
			return Collections.unmodifiableSet(this.invertedIndex.get(word).get(location));
		}
	}

	/**
	 * This is search function that is calling partial or exact function to make
	 * search
	 * 
	 * @param queries
	 * @param exact
	 * @return exact / partial search
	 */
	public ArrayList<SearchResult> search(Collection<String> queries, boolean exact) {
		return exact ? exactSearch(queries) : partialSearch(queries);
	}

	/**
	 * This function process exact search
	 * 
	 * @param queries to search
	 * @return results of the search
	 */
	public ArrayList<SearchResult> exactSearch(Collection<String> queries) {
		ArrayList<SearchResult> results = new ArrayList<>();
		HashMap<String, SearchResult> track = new HashMap<>();

		for (String query : queries) {
			if (invertedIndex.containsKey(query)) {
				searchHelper(results, query, track);
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * This function process partial search
	 * 
	 * @param queries to search
	 * @return results of the search
	 */
	public ArrayList<SearchResult> partialSearch(Collection<String> queries) {
		ArrayList<SearchResult> results = new ArrayList<>();
		HashMap<String, SearchResult> track = new HashMap<>();

		for (String query : queries) {
			for (String word : this.invertedIndex.tailMap(query).keySet()) {
				if (word.startsWith(query)) {
					searchHelper(results, word, track);
				} else {
					break;
				}
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * This function is a helper method used in search methods.
	 * 
	 * @param results list of objects.
	 * @param word    to searched.
	 * @param track   for updating and track counts and scores.
	 */
	private void searchHelper(ArrayList<SearchResult> results, String word, Map<String, SearchResult> track) {
		for (String location : this.invertedIndex.get(word).keySet()) {
			if (track.containsKey(location)) {
				track.get(location).updateCount(word);
			} else {
				SearchResult result = new SearchResult(location);
				result.updateCount(word);
				track.put(location, result);
				results.add(result);
			}
		}
	}

	/**
	 * This function is inner SearchResult class that implements Comparable.
	 */
	public class SearchResult implements Comparable<SearchResult> {

		/**
		 * This will hold the location of the search result.
		 */
		private final String location;

		/**
		 * This will hold the count of the search result.
		 */
		private int count;

		/**
		 * This will hold the score of the search result.
		 */
		private double score;

		/**
		 * This function is Constructor for SearchResult objects.
		 * 
		 * @param location to construct
		 * 
		 */
		public SearchResult(String location) {
			this.location = location;
			this.count = 0;
			this.score = 0;
		}

		/**
		 * This function is to debug constructor.
		 * 
		 * @param location set
		 * @param count    set
		 * @param score    set
		 */
		public SearchResult(String location, int count, double score) {
			this.location = location;
			this.count = count;
			this.score = score;
		}

		/**
		 * This function is to update the count
		 * 
		 * @param word to update
		 */
		private void updateCount(String word) {
			this.count += invertedIndex.get(word).get(this.location).size();
			this.score = (double) this.count / counts.get(this.location);
		}

		/**
		 * This function is getter for the count data member.
		 * 
		 * @return the count data member
		 */
		public int getCount() {
			return this.count;
		}
		
		/**
		 * 
		 * @return string with location
		 */
		public String getLocation() {
			return this.location;
		}

		/**
		 * This function is getter for the score data member.
		 * 
		 * @return the score
		 */
		public double getScore() {
			return this.score;
		}

		/**
		 * This function check if another results location is the same as this ones.
		 * 
		 * @param other location
		 * @return true if same;
		 */
		public boolean sameLocation(SearchResult other) {
			return this.location.compareTo(other.location) == 0;
		}

		@Override
		public String toString() {
			String out = "";

			out += "\"where\": ";
			out += "\"" + this.location + "\",\n";

			out += "\"count\": ";
			out += "\"" + this.count + "\",\n";

			out += "\"score\": ";
			out += "\"" + this.score + "\"";

			return out;
		}

		/**
		 * @return This function formats string to write into file for location.
		 */
		public String getWhereString() {
			return ("\"where\": " + "\"" + this.location + "\",");
		}

		/**
		 * @return This function formats string to write into file for count.
		 */
		public String getCountString() {
			return ("\"count\": " + this.count + ",");
		}

		/**
		 * @return This function formats string to write into file for location.
		 */
		public String getScoreString() {
			return ("\"score\": " + String.format("%.8f", this.score));
		}

		@Override
		public int compareTo(SearchResult o) {
			double scoreDiff = this.score - o.score;

			if (scoreDiff != 0) {
				return scoreDiff > 0 ? -1 : 1;
			} else {
				int countDiff = this.count - o.count;

				if (countDiff != 0) {
					return countDiff > 0 ? -1 : 1;
				} else {
					return (this.location.toLowerCase().compareTo(o.location.toLowerCase()));
				}
			}
		}
	}

}
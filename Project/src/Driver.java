import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;


/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author Alex Kowalczuk
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class Driver {

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		Instant start = Instant.now();
		
		int numThreads = 1;
		
		ArgumentParser parse = new ArgumentParser(args);
		InvertedIndex index;
		InvertedIndexBuilder builder;
		QueueInterface queryHandler;
		
		WebCrawler webCrawler;
		SearchServlet search;
		
		if (parse.hasValue("-threads") || parse.hasValue("-url") || parse.hasValue("-port")) {
			try {
				numThreads = Integer.parseInt(parse.getString("-threads"));
				if (numThreads <= 0) {
					numThreads = 5;
				}
			} catch (Exception e) {
				numThreads = 5;
			}
			
			ThreadedInvertedIndex threadSafe = new ThreadedInvertedIndex();
			index = threadSafe;
			builder = new ThreadedInvertedIndexBuilder(threadSafe, numThreads);
			queryHandler = new ThreadedQueryHandler(threadSafe, numThreads);
			
			if (parse.hasValue("-limit")) {
				webCrawler = new WebCrawler(threadSafe, numThreads, Integer.parseInt(parse.getString("-limit")));
			} else {
				webCrawler = new WebCrawler(threadSafe, numThreads, 50);
			}
			
			if (parse.hasValue("-url")) {
				try {
					URL seedURL = new URL(parse.getString("-url"));
					webCrawler.traverse(seedURL);
				} catch (Exception e) {
					System.out.println("Something went wrong with URL:" + parse.getString("-url"));
				}
			}
			
			if (parse.hasValue("-port")) {
				search = new SearchServlet(queryHandler, threadSafe);

				int port;

				try {
					port = Integer.parseInt(parse.getString("-port"));
				} catch (Exception e) {
					port = 8090;
				}

				try {
					ServletContextHandler servletContext = null;

					servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
					servletContext.setContextPath("/");

					DefaultHandler defaultHandler = new DefaultHandler();
					defaultHandler.setServeIcon(true);

					ContextHandler defaultContext = new ContextHandler("/favicon.ico");
					defaultContext.setHandler(defaultHandler);

					ServletHolder sholder = new ServletHolder(search);

					ServletHandler handler = new ServletHandler();
					handler.addServletWithMapping(sholder, "/");

					Server server = new Server(port);
					server.setHandler(handler);
					server.start();
					server.join();

				} catch (Exception e) {
					System.err.println("Jetty server Did not work");
				}
			}
		} else { 
			index = new InvertedIndex();
			builder = new InvertedIndexBuilder(index);
			queryHandler = new QueryHandler(index);
			
		}
		
		if (parse.hasFlag("-path") && parse.getPath("-path") != null) {
			Path path = parse.getPath("-path");
			try {
				builder.traversePath(path);
			} 
			catch (IOException e) {
				System.out.println("Path cant be traversed: " + path); 
			}
		}
		
		if (parse.hasFlag("-counts")) {
			Path path = parse.getPath("-counts", Path.of("counts.json"));
			try {
				SimpleJsonWriter.asObject(index.getCounts(), path);
			} 
			catch (IOException e) {
				System.out.println("There was issue with counting: " + path);
			}
		}
		
		if (parse.hasFlag("-index")) {
			Path path = parse.getPath("-index", Path.of("index.json"));
			try {
				index.printIndex(path);
			}
			catch (IOException e) {
				System.out.println("Unable to write the inverted index to JSON at path: " + path);
			}
		}
		
		if(parse.hasFlag("-query") && parse.getPath("-query") != null) {
			Path queryPath = parse.getPath("-query");
			try {
				queryHandler.parseQuery(queryPath, parse.hasFlag("-exact"));
			} catch(IOException e) {
				System.out.println("There was issue with reading file: " + queryPath.toString());
			} catch(Exception r) {
				System.out.println("There was issue with making file: " + queryPath.toString());
			}	
		}
		
		
		if (parse.hasFlag("-results")) {
			Path path = parse.getPath("-results", Path.of("results.json"));
			
			try {
				queryHandler.writeQuery(path);
				
			} catch(IOException e) {
				System.out.println("There was issue with results: " + path);
			}
			
		}
		
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}

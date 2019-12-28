import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;

/**
 * @author alex
 *
 **/

/**
 * This Servlet responsible for handling search history and visit history, using
 * cookies.
 */

public class CookiesConfigServlet extends HttpServlet
{
	
	/**
	 * This is the query
	 */
	private QueueInterface query;

	/** The thread-safe data structure to use for storing messages. */
	private ConcurrentLinkedQueue<String> messages;

	
	/**
	 * The number of searches
	 */
	private int searches = 0;
	

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");

		out.printf("<html>%n");
		out.printf("<head><meta charset=\"utf-8\">\r\n    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\r\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><title>Search Engine</title><link href=\"/bootstrap/css/bootstrap.min.css\" rel=\"stylesheet\"></head>%n");
		out.printf("<body>%n");

		Map<String, Cookie> cookies = getCookieMap(request);

		out.print("<h2>Search History: </h2>");
		Cookie queries = cookies.get("queries");
		if (queries != null)
		{
			out.println("<p>");
			String decoded = URLDecoder.decode(queries.getValue(), StandardCharsets.UTF_8.name());
			System.out.println(decoded);
			for (String query: decoded.split(","))
			{
				if (!query.trim().isEmpty())
				{
					out.printf("[%s]<br/>", query);
				}
			}
			out.println("</p>");
		} else {
			out.print("<p>No search history</p>");
			String encoded = URLEncoder.encode(",", StandardCharsets.UTF_8.name());
			queries = new Cookie("queries", encoded);

		}
		
		out.printf("<form method=\"post\" action=\"%s\">%n", request.getRequestURI());
		out.printf("\t<input type=\"submit\" name=\"clear\" value=\"Clear search history\">%n");
		out.printf("</form>%n");

		out.print("<h2>Visit History: </h2>");
		Cookie visited = cookies.get("visited");
		if (visited != null)
		{
			out.println("<p>"); 
			String decoded = URLDecoder.decode(visited.getValue(), StandardCharsets.UTF_8.name());
			for (String url: decoded.split(","))
			{
				if (!url.trim().isEmpty())
				{
					out.println("<a href=\"" + url + "\"> " + url + "</a><br/>");
				}
			}
			out.println("</p>");
		} else
		{
			out.print("<p>No visit history</p>");
			String encoded = URLEncoder.encode(",", StandardCharsets.UTF_8.name());
			visited = new Cookie("visited", encoded);
		}
		response.addCookie(queries);
		response.addCookie(visited);
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);


		//long st = System.nanoTime();
		String message = request.getParameter("search");
		
		String b = request.getParameter("exact");
		var checkBox = request.getParameter("exact");
		System.out.println("BOX:   " + checkBox);
		System.out.println("BUTTON: '" + b + "'");
		
		message = message == null ? "" : message;
		 
		//username = username == null ? "" : username;
		
		// Avoid XSS attacks using Apache Commons Text IMPORTANT ^^ 
		// username = StringEscapeUtils.escapeHtml4(username);
		
		message = StringEscapeUtils.escapeHtml4(message);
		
		response.getWriter();

		String formatted = String.format("<p1> %s </p1>\");\n", messages);
		String searchstr = request.getParameter("button");
		System.out.println(searchstr);
		boolean search = (b != null && b.contains("on")) ? true : false;
		System.out.println("SEARCH: " + search);
		
		/*
			if (searchstr.equals("True") && !searchstr.isBlank() && !searchstr.isEmpty() && !searchstr.equals(null)) {
				System.out.println("in exact");
				System.out.println(searchstr);
				search = true;
			} */
		
		System.out.println("boolean for search " + search);
		query.parseQuery(message, search);
		System.out.println("boolean for search " + search);

		List<ThreadedInvertedIndex.SearchResult> theresult = query.getResults(message);
		for (ThreadedInvertedIndex.SearchResult r: theresult) {
			System.out.println("search results:" + r);
		}
		
		if (theresult == null || theresult.isEmpty()) {
			searches = 0;
			messages.clear();
			formatted = String.format(
					"					<i class=\"fas fa-quote-left has-text-grey-light\"></i> %s <i class=\"fas fa-quote-right has-text-grey-light\"></i>%n"
							+ "					<p class=\"has-text-grey is-size-7 has-text-right\"></p>%n",
					"The String: " + request.getParameter("search") + " Does not exist", getDate());
			messages.add(formatted);
			
		} else {
			messages.clear();
			searches = 0;
			for (ThreadedInvertedIndex.SearchResult result : theresult) {
				formatted = String.format(
						"<a href=\"%s\">%s</a>"
								+ "					<p class=\"has-text-grey is-size-7 has-text-right\">%s</p>%n",
						result.getLocation(), result.getLocation(), getDate());
				searches++;
				messages.add(formatted);
			}
			//seconds =  (System.nanoTime() - st) / 1000000;
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
	}

	/**
	 * Gets the cookies form the HTTP request, and maps the cookie name to the
	 * cookie object.
	 *
	 * @param request - HTTP request from web server
	 * @return map from cookie key to cookie value
	 */
	public static Map<String, Cookie> getCookieMap(HttpServletRequest request)
	{
		HashMap<String, Cookie> map = new HashMap<>();
		Cookie[] cookies = request.getCookies();

		if (cookies != null)
		{
			for (Cookie cookie: cookies)
			{
				map.put(cookie.getName(), cookie);
			}
		}

		return map;
	}

	/**
	 * Clears all of the cookies included in the HTTP request.
	 *
	 * @param request - HTTP request
	 * @param response - HTTP response
	 */
	public void clearCookies(HttpServletRequest request, HttpServletResponse response)
	{

		Cookie[] cookies = request.getCookies();

		if (cookies != null)
		{
			for (Cookie cookie: cookies)
			{
				cookie.setValue(null);
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}
	}
	

	/**
	 * Returns the date and time in a long format. For example: "12:00 am on
	 * Saturday, January 01 2000".
	 *
	 * @return current date and time
	 */
	private static String getDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());

	}
}
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;

/**
 * Search engine display using the Bulma CSS framework.
 *
 */
public class SearchServlet extends HttpServlet {

	/**
	 * This is the serial ID
	 */
	private static final long serialVersionUID = 1L;

	/** The title to use for this webpage. */
	private static final String TITLE = "THE BEST SEARCH BY ALEX :)";
	
	/** The lucky search buttom to use for this webpage. */
	private static final String LUCKY = "I AM FEELING LUCKY";

	/** The thread-safe data structure to use for storing messages. */
	private ConcurrentLinkedQueue<String> messages;

	/**
	 * This is the query
	 */
	private QueueInterface query;

	/**
	 * The number of searches
	 */
	private int searches = 0;
	
	/**
	 * The number of visits
	 */
	private int visit = 0;

	/**
	 * The time it took
	 */
	private long seconds;
	
	/**
	 * Initializes this message board. Each message board has its own collection of
	 * messages.
	 *
	 * @param queryBuilder The query builder
	 * @param invertedIndex 
	 */
	public SearchServlet(QueueInterface queryBuilder, ThreadedInvertedIndex invertedIndex) {
		super();
		this.query = queryBuilder;
		messages = new ConcurrentLinkedQueue<>();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
 
		PrintWriter out = response.getWriter();

		out.printf("<!DOCTYPE html>%n");
		out.printf("<html>%n");
		out.printf("<body style=\"background-color:powderblue;\">");
		out.printf("<head>%n");
		out.printf("	<meta charset=\"utf-8\">%n");
		out.printf("	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">%n");
		out.printf("	<title>%s</title>%n", TITLE);
		out.printf(
				"	<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/bulma/0.7.4/css/bulma.min.css\">%n");
		out.printf(
				"	<script defer src=\"https://use.fontawesome.com/releases/v5.8.1/js/all.js\" integrity=\"sha384-g5uSoOSBd7KkhAMlnQILrecXvzst9TdC09/VM+pjDTCM+1il8RHz5fKANTFFb+gQ\" crossorigin=\"anonymous\"></script>%n");
		out.printf("</head>%n");
		out.printf("%n");
		out.printf("<body>%n");
		out.printf("	<section class=\"\">%n");
		out.printf("	  <div class=\"hero-body\">%n");
		out.printf("	    <div class=\"container\">%n");
		out.printf("<figure class=\"image is-15x15\" style=\"text-align:center\">\n"
				+ "  <center><img src=\"https://i.ibb.co/ZGvv5wL/logo-Alex-Web.png\" style=\"width:150px;height:150px;\" ></center>\n" //LOGO Image
				+ "</figure>");
		
		out.printf("	      <h1 class=\"title\" style=\"text-align:center\">%n");
		out.printf(	        TITLE + "%n");
		out.printf("	      </h1>%n");
		out.printf("<p1><center> This search engine is a web-based tool that enables users to locate information on the World Wide Web. "
				+ "Popular examples of search engines are The best in the world Alex Search, Google, Yahoo!, and MSN Search. "
				+ "The information gathered by the spiders is used to create a searchable index of the Web. </center></p1>"); //DESCRIPTION of the website.
		out.printf("<br>");
		out.println("<p><center>Welcome, this is your " + visit + " visit, your last visit was on " + getShortDate() + "</center></p>");
		
		out.printf("	<section class=\"section\">%n");
		out.printf("		<div class=\"container\">%n");
		out.printf("      <h2 class=\"subtitle\">\n");

		out.printf("      </h2>");
		out.printf("	  </div>%n");
		out.printf("	</section>%n");

		out.printf("	    </div>%n");
		out.printf("	  </div>%n");
		out.printf("	</section>%n");

		out.printf("<h2 class=\"subtitle\">\n");
		out.printf("%n");
		out.printf("	<section class=\"section\">%n");
		out.printf("		<div class=\"container\">%n");
		out.printf("%n");
		out.printf("			<form method=\"%s\" action=\"%s\">%n", "POST", request.getServletPath(), visit ++);
		out.printf("				<div class=\"field\">%n");
		out.printf("					<div class=\"control has-icons-left\">%n");
		out.printf(
				"						<input class=\"input\" type=\"text\" name=\"%s\" placeholder=\"Please put your search right here...\">%n",
				"search");
		out.printf("						<span class=\"icon is-small is-left\">%n");
		out.printf("							<i class=\"fas fa-search\"></i>%n");
		out.printf("						</span>%n");
		out.printf("<p><center> <a href=\"/history\">view history</a>&nbsp;&nbsp;&nbsp;<a href=\"/crawler\">web crawler</a></center></p>\n%n");
		out.printf("					</div>%n");
		out.printf("				</div>%n");
		out.printf("%n");
		out.printf("%n");

		out.printf("<center><input type = \"checkbox\" name = \"exact\" id = \"exact\" > Exact</center>");
		out.printf("<center><input type = \"checkbox\" name = \"partial\" id = \"partial\" > Partial</center>");

		out.printf("%n");
		out.printf("				<div class=\"control\">%n");
		out.printf("			    <center><button class=\"button is-danger\" type=\"submit\">%n");
		out.printf("						<i class=\"fas fa-tachometer-alt\"></i>%n");
		out.printf("						&nbsp;%n");
		out.printf(TITLE + "%n");
		out.printf("					</button></center>%n");
		out.printf("<br>");
		out.printf("<p><center>Do you feel lucky today?</center></p>");
		out.printf("<br>");
		out.printf("			    <center><button class=\"button is-danger\" type=\"submit\">%n");
		out.printf("						<i class=\"fas fa-tachometer-alt\"></i>%n");
		out.printf("						&nbsp;%n");
		out.printf(LUCKY + "%n");
		out.printf("					</button></center>%n");
		out.printf("			  </div>%n");
		out.printf("			</form>%n");
		out.printf("		</div>%n");

		out.printf("%n");
		out.printf("	<section class=\"section\">%n");
		out.printf("		<div class=\"container\">%n");

		//I could make that function better, I know....
		if (messages.isEmpty()) {
			out.printf("ops, nothing here...");

		} else {
			for (String message : messages) {
				out.printf("				<div class=\"box\">%n");
				out.printf(message);
				out.printf("				</div>%n");
				out.printf("%n");
			}
		}
		out.printf("			</div>%n");
		out.printf("%n");
		out.printf("		</div>%n");
		out.printf("	</section>%n");
		out.printf("	<footer class=\"footer\">%n");
		out.printf("	  <div class=\"content has-text-centered\">%n");
		out.printf("	    <p>%n");
		out.printf("	      This request was handled by thread %s.%n", Thread.currentThread().getName());
		out.printf("	    </p>%n");
		out.printf("	      <p>");
		out.printf("					<i class=\"fas fa-calendar-alt\"></i>%n");
		out.printf("					&nbsp;Updated %s%n", getDate(), getShortDate());
		out.printf("	      </p>%n");
		out.printf("	    <p>%n");
		out.printf(seconds + " ms.</p1>%n");
		out.printf("	    </p>%n");
		out.printf("%n");
		out.printf("	    <p>%n");
		out.printf("<p1>Results Found: " + searches + "</p1>%n");
		out.printf("	    </p>%n");
		
		out.printf("	  </div>");
		out.printf("	</footer>");
		out.printf("</body>");
		out.printf("</html>");

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);


		long st = System.nanoTime();
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
			seconds =  (System.nanoTime() - st) / 1000000;
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
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
	 
	/**
	 * Returns the current date and time in a short format.
	 *
	 * @return current date and time
	 */
	public static String getShortDate()
	{
		String format = "yyyy-MM-dd hh:mm a";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(Calendar.getInstance().getTime());
	}

}
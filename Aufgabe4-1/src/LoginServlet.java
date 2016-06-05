

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Zugriff auf das Session Objekt
		HttpSession session = request.getSession();
		
		
		LinkedList <String> userlist = (LinkedList<String>) session.getAttribute("list");
		if(userlist == null)
		{
			userlist = new LinkedList();
			session.setAttribute("list", userlist);
		}
		
		String curUser = request.getParameter("username");
		if(curUser != null && !curUser.trim().isEmpty())
		{
			userlist.add(curUser);
		}
		
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		try {
			out.println("<html>");
			out.println("<head>");
			out.println("<meta charset=\"utf-8\">");
			out.println("<title>Login Formular</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<h1>Loginfenster</h1>");
			out.println("<text id=\"user\">Username: </text>");
			out.println("<form method=\"POST\" action=\"LoginServlet\">");
			out.println("<input type =\"text\" name=\"username\" id=input></input>");
			out.println("<button id=button type=\"submit\">Login</button>");
			out.println("</form>");
			//Überprüft ob ein gültiger Username eingegeben wurde
			if(userlist.isEmpty()) {
				out.println("<h2>"+"Keine Einträge"+"</h2>");
			}
			else {
				out.println("<h2>Userliste: </h2>");
				out.println("<table>");
				for(String user : userlist)
				{
					out.println("<tr><td>"+user+"</td></tr>");
				}
				out.println("</table>");
			}
			out.println("</body>");
			out.println("</head>");
		} finally {
			out.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

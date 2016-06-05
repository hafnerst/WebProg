package servlets;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sun.javafx.collections.MappingChange;
import com.sun.javafx.collections.MappingChange.Map;
import com.sun.org.apache.xml.internal.resolver.Catalog;

import application.Player;
import application.Quiz;
import error.QuizError;
import loader.CatalogLoader;
import loader.FilesystemLoader;
import loader.LoaderException;

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
		
		Quiz quiz = Quiz.getInstance();
		QuizError error = new QuizError();
		
		java.util.Map<String, application.Catalog> catalogMap = null;
		FilesystemLoader fileLoader = new FilesystemLoader("xml");
		
		quiz.initCatalogLoader(fileLoader);
		
		try 
		{
			catalogMap =  quiz.getCatalogList();
		} 
		catch (LoaderException e) 
		{
			e.printStackTrace();
			System.out.println("Failed to load Catalogs");
		}
		
		session.setAttribute("catalogs", catalogMap);
			    
		String curUser = request.getParameter("username");
		if(curUser != null && !curUser.trim().isEmpty())
		{
			quiz.createPlayer(curUser, error);
		}
		
		Collection<Player> playerlist = quiz.getPlayerList();
		session.setAttribute("players", playerlist);
		
		
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("loginbereich.jsp");
		requestDispatcher.forward(request, response);		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

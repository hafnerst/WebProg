package listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import application.Catalog;
import application.Quiz;
import loader.FilesystemLoader;
import loader.LoaderException;
import loader.XMLloader;
 
@WebListener
public class QuizServletContextListener implements ServletContextListener {
 
    public void contextInitialized(ServletContextEvent servletContextEvent) {
    	
    	System.out.println("Erzeuge Quiz");
    	ServletContext cntxt = servletContextEvent.getServletContext();

    	XMLloader xmlLoader = new XMLloader("xml");
        Quiz quiz = Quiz.getInstance();
		quiz.initCatalogLoader(xmlLoader);
    }
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
     
}
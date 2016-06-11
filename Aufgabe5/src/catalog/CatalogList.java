package catalog;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import javax.xml.transform.*;
import org.w3c.dom.*;

import application.Quiz;
import application.Catalog;
import loader.FilesystemLoader;
import loader.LoaderException;

@WebServlet("/catalogList")
public class CatalogList extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	
	public CatalogList()
	{
		super();
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Quiz quiz = Quiz.getInstance();
		Map<String, Catalog> catalogMap = null;

		try 
		{
			catalogMap =  quiz.getCatalogList();
		} 
		catch (LoaderException e) 
		{
			e.printStackTrace();
			System.out.println("Failed to load Catalogs");
		}
				
		String xmlString = this.saveToXML(catalogMap);
	
		PrintWriter writer = response.getWriter();	
		writer.print(xmlString);
	}
	
	public String saveToXML(Map<String, Catalog> catalogMap) 
	{
	    Document dom = null;
	    DOMSource domSource = null;
	    String output = null;

	    // instance of a DocumentBuilderFactory
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    try 
	    {
	        // use factory to get an instance of document builder
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        // create instance of DOM
	        dom = db.newDocument();

	        // create the root element
	        Element rootEle = dom.createElement("catalogs");
	        int count = 1;
	        for(String cat : catalogMap.keySet()) 
	        {
		        // create data elements and place them under root
	        	Element e = dom.createElement("name");
	        	e.setAttribute("id", count+"");
		        e.appendChild(dom.createTextNode(cat));
		        rootEle.appendChild(e);
		        count++;
	        }

	        dom.appendChild(rootEle);

	        try 
	        {
	            Transformer tr = TransformerFactory.newInstance().newTransformer();
	            tr.setOutputProperty(OutputKeys.INDENT, "yes");
	            tr.setOutputProperty(OutputKeys.METHOD, "xml");
	            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	            tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "catalog.dtd");
	            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

	            // send DOM to file
	            domSource = new DOMSource(dom);
	            StringWriter writer = new StringWriter();
	            tr.transform(domSource, new StreamResult(writer));
	            output = writer.getBuffer().toString().replaceAll("\n|\r", "");

	        } 
	        catch (TransformerException te) 
	        {
	            System.out.println(te.getMessage());
	        }
	    } 
	    catch (ParserConfigurationException pce) 
	    {
	        System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
	    }
	    return output;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
}
package mainpackage;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.jdom2.JDOMException;

import application.Catalog;
import application.Question;
import loader.FilesystemLoader;
import loader.LoaderException;

public class Main {
	
	private final static int numberOfAnswers = 4;

	public static void main(String[] args) throws JDOMException, IOException, LoaderException {	// IOException und JDOMException müssen wegen SAXBuilder abgefangen werden
		
		System.out.println("Geben Sie einen Ordner im bin-Verzeichnis an wie z.B. questioncatalogs: ");
		Scanner scanner = new Scanner(System.in);
		String selectedFolder = scanner.nextLine();
		
		FilesystemLoader loader = new FilesystemLoader(selectedFolder);
		
		Map<String, Catalog> catalogMap = null;
		int counter = 1;
		
		try {
			catalogMap = loader.getCatalogs();
		} catch (LoaderException e) {
			e.printStackTrace();
			System.out.println("Failed to load Catalogs");
		}
		
		System.out.println("Katalogauswahl");
	    for(String key : catalogMap.keySet())
	    {
	      System.out.println("Catalog "+counter+": " + key);
	      counter++;
	    }
	    System.out.println("\n__________________________________________________________\n");
	    
	    System.out.println("Wählen Sie bitte einen Katalog aus: ");
	    String selectedCatalog = scanner.nextLine();
	    
	    Collection<Catalog> catCollection = catalogMap.values();
	    List<Question> questionList = null;
	    
	    counter = 0;
	    for (Catalog cat : catCollection) {
	    	if(cat.getName().equals(selectedCatalog))
	    	{
	    		questionList = cat.getQuestions();
	    		counter++;
	    	}
	    }
	    if(counter == 0)
	    {
	    	System.out.println("Kein Katalog gefunden. Programm wird beendet.");
	    	return;
	    }
	    
	    counter = 1;
	    for(Question q: questionList)
	    {
	      System.out.println("Frage "+counter+": "+q.getQuestion());
	      List<String> curAnswers = q.getAnswerList();
		  for(String s: curAnswers)
		  {
			  System.out.println("- "+s);
		  }
		  System.out.println("");
	      counter++;
	    }
	}

}

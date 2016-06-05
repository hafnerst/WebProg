package loader;

import org.jdom2.*;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.input.SAXBuilder;

import application.Catalog;
import application.Question;

public class FilesystemLoader implements CatalogLoader {

    /**
    * RegEx to capture the question block.
    * <p>
    * Captures three groups:
    * <p>
    *  1. Group: Contains the question<br>
    *  2. Group (optional): Timeout<br>
    *  3. Group: Answer block (all possible answers)<br>
    */
    private static final String QUESTION_BLOCK_REGEX =
        "(.+)\n(?:TIMEOUT: ([0-9]+)\n)??((?:[+-] .+\n){4}?)";
    /**
     * RegEx captures the individual answers in the captured answer block
     * from the more general expression above.
     * <p>
     * There are two capture groups:
     * <p>
     *  1. Group: +/-, which states if the answer is true or false<br>
     *  2. Group: Contains the answer<br>
     */
    private static final String ANSWER_REGEX = "([+-]) (.+)\n";

    private final Pattern blockPattern = Pattern.compile(QUESTION_BLOCK_REGEX);
    private final Pattern questionPattern = Pattern.compile(ANSWER_REGEX);

    private File[] catalogDir;
    private final Map<String, Catalog> catalogs =
        new HashMap<String, Catalog>();
    private final String location;

    public FilesystemLoader(String location) {
        this.location = location;
        }

    @Override
    public Map<String, Catalog> getCatalogs() throws LoaderException {

        if (!catalogs.isEmpty()) {
            return catalogs;
        }

        // Construct URL for package location
        URL url = this.getClass().getClassLoader().getResource(location);

        File dir;
        try {
            // Make sure the Java package exists
            if (url != null) {
                dir = new File(url.toURI());
            } else {
                dir = new File("/");
            }
        } catch (URISyntaxException e) {
            // Try to load from the root of the classpath
            dir = new File("/");
        }

        // Add catalog files
        if (dir.exists() && dir.isDirectory()) {
            this.catalogDir = dir.listFiles(new CatalogFilter());
            for (File f : catalogDir) { 
                catalogs.put(f.getName(),
                    new Catalog(f.getName(), new QuestionFileLoader(f)));
            }
        }

        return catalogs;
    }

    @Override
    public Catalog getCatalogByName(String name) throws LoaderException {
        if (catalogs.isEmpty()) {
            getCatalogs();
        }

        return this.catalogs.get(name);
    }

    /**
     * Filter class for selecting only files with a .cat extension.
     *
     * @author Simon Westphahl
     *
     */
    private class CatalogFilter implements FileFilter {

        /**
         * Accepts only files with a .cat extension.
         */
        @Override
        public boolean accept(File pathname) {
            if (pathname.isFile() && pathname.getName().endsWith(".xml"))
                return true;
            else
                return false;
        }

    }

    private class QuestionFileLoader implements QuestionLoader {

        private final File catalogFile;
        private final List <Question> questions = new ArrayList<Question>();
        private final static int numberOfAnswers = 4;

        public QuestionFileLoader(File file) {
            catalogFile = file;
        }
        @Override
        public List<Question> getQuestions(Catalog catalog)
            throws LoaderException {
        	
        	Document doc = null;

            if (!questions.isEmpty()) {
                return questions;
            }
            
            String filename = "bin\\questioncatalogs\\"+catalog.getName();
            try {
            	doc = new SAXBuilder().build( filename );	//JDOM-Dokument mit Hilfe von SAXBuilder erstellen
            }
            catch(Exception e){
    			e.printStackTrace();
    		}
            
            Element curCatalog = doc.getRootElement();			//Wurzel-Element abfragen
    		List<Element> questionList = curCatalog.getChildren();			//JDOM-Baum in Liste speichern
    		
    		for(int i =0; i<questionList.size(); i++)		//Durchlaufe jede Frage
    		{
    			Question curQuestion = new Question(questionList.get(i).getChild("title").getAttributeValue("text"));
    			for(int j=0; j<numberOfAnswers; j++)	//Durchlaufe alle Antworten
    			{
    				if(questionList.get(i).getChild("answers").getChild("answer"+(j+1)).getAttributeValue("correct").equals("true"))
    				{
    					//Füge richtige Antwort zu Objekt Question hinzu
    					curQuestion.addAnswer(questionList.get(i).getChild("answers").getChild("answer"+(j+1)).getAttributeValue("answertext"));
    				}
    				else
    				{
    					//Füge falsche Antwort zu Objekt Question hinzu
    					curQuestion.addBogusAnswer(questionList.get(i).getChild("answers").getChild("answer"+(j+1)).getAttributeValue("answertext"));
    				}
    			}
    			//Füge aktuelle Question zur questionlist hinzu
    			questions.add(curQuestion);
    		}

            return questions;
        }

    }
}

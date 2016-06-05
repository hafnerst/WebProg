package websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import application.Player;
import application.Quiz;
import error.QuizError;

@ServerEndpoint("/chat")
public class ExchangeEndpoint {
	private Quiz quiz = Quiz.getInstance();
	private QuizError quizError = new QuizError(); 
	
	//Wird beim Anmelden eines Clients aufgerufen
	@OnOpen
	public void opened(Session session)
	{
		ConnectionManager.addTmpSession(session);
		if(quiz.getCurrentCatalog() != null)
		{
			JSONObject catalogChange = new JSONObject();
			catalogChange.put("Type", "5");
			catalogChange.put("Length", quiz.getCurrentCatalog().getName().length());
			catalogChange.put("Message", quiz.getCurrentCatalog().getName());
			//sendToAllSessions(catalogChange);
		}
	}
	
	@OnClose
	public void closed(Session session) {
		if(!ConnectionManager.removeTmpSession(session)) {
			Player tmp = ConnectionManager.getPlayer(session);
			if(tmp.getId() == 0) { // Superuser left
				quiz.removePlayer(tmp, quizError);
				ConnectionManager.removeSession(session);
				JSONObject error = new JSONObject();
				error.put("Type", "255");
				error.put("Length", 1 + 14);
				error.put("Subtype", "1");
				error.put("Message", "Superuser left");
				//sendToAllSessions(error);
				
			} else {
				quiz.removePlayer(tmp, quizError);
				ConnectionManager.removeSession(session);
			}
		}
	}

	@OnMessage
	public void receiveMessage(Session session, String message) throws ParseException {
		System.out.println("Received Message. "+ message);
		JSONObject jsonMessage;

		jsonMessage = (JSONObject) new JSONParser().parse(message);


		int msgType = Integer.parseInt((String) jsonMessage.get("Type"));
		System.out.println("Nachricht von Client: " + jsonMessage);
	
		// Message vom Type: LoginRequest
		if(msgType == 1) {
			int msgLength = Integer.parseInt((String) jsonMessage.get("Length").toString());
			if(msgLength > 0) {
				Player tmpPlayer = quiz.createPlayer((String) jsonMessage.get("Name"), quizError);
				if(tmpPlayer == null) { // Fehlerfall
					JSONObject error = new JSONObject();
					error.put("Type", "255");
					error.put("Length", 1 + quizError.getDescription().length());
					error.put("Subtype", "1");
					error.put("Message", quizError.getDescription());
					System.out.println(error);
					sendJSON(session, error);
				} else { // Username in Ordnung
					JSONObject player = new JSONObject();
					player.put("Type", "2");
					player.put("Length", "1");
					player.put("ClientID", tmpPlayer.getId());
					ConnectionManager.addSession(session, tmpPlayer);
					ConnectionManager.removeTmpSession(session);
					sendJSON(session, player);

				}
			} else { // kein Username eingegeben
				JSONObject error = new JSONObject();
				error.put("Type", "255");
				error.put("Length", 17);
				error.put("Subtype", "1");
				error.put("Message", "Username required");
				sendJSON(session, error);
			}	
		}
	} 
	
	public static synchronized void sendJSON(Session session, JSONObject tmp) {
		try {
			session.getBasicRemote().sendText(tmp.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void sendToAllSessions(JSONObject error) {
		// Alle aktiven Sessions durchgehen
		Set<Session> tmpMap = ConnectionManager.getSessions();
		for(Iterator<Session> iter = tmpMap.iterator(); iter.hasNext(); ) {
			Session s = iter.next();
			sendJSON(s, error);
		}
		
		// Alle temp Sessions durchgehen, um die Spielerliste aktuell anzuzeigen
		List<Session> tmpSessions = ConnectionManager.getTmpSessions();
		if(tmpSessions.size() > 0) {
			for (Session tempS : tmpSessions) {
				sendJSON(tempS, error);
			}
		}
	}
}

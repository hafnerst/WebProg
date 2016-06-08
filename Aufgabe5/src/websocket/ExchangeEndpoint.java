package websocket;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import application.Player;
import application.Question;
import application.Quiz;
import error.QuizError;
import thread.TimerTaskThread;

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
			sendToAllSessions(catalogChange);
		}
		if(ConnectionManager.getSessionCount() < 4) {
			sendPlayerlist();
		}
		System.out.println("In Open Funktion");
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
				sendToAllSessions(error);
				
			} else {
				quiz.removePlayer(tmp, quizError);
				ConnectionManager.removeSession(session);
			}
		}
		this.sendPlayerlist();
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
			if(ConnectionManager.getSessionCount() >= 4)
			{
				System.out.println("Maximale Spielerzahl");
			}
			else {
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
						sendPlayerlist();
						System.out.println("Spieler erstellt");
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
		//CatalogChanged
		if(msgType == 5) {
			JSONObject catalogChange = new JSONObject();
			catalogChange.put("Type", "5");
			catalogChange.put("Length", jsonMessage.get("Length").toString());
			catalogChange.put("Message", jsonMessage.get("Message"));
			quiz.changeCatalog(ConnectionManager.getPlayer(session), jsonMessage.get("Message").toString(), quizError);
			sendToAllSessions(catalogChange);
		}
		//StartGame Message
		if(msgType == 7) {
			//quiz.startGame(ConnectionManager.getPlayer(session), quizError);
			//if(quiz.startGame(ConnectionManager.getPlayer(session), quizError))
			//{
				JSONObject gameStarted = new JSONObject();
				gameStarted.put("Type", "7");
				gameStarted.put("Length", jsonMessage.get("Length").toString());
				gameStarted.put("Message", jsonMessage.get("Message"));
				sendToAllSessions(gameStarted);
			/*}
			else
			{
				System.out.println("Game konnte nicht gestartet werden!");
			}*/
		}
		//QuestionRequest
		if(msgType == 8) {
			System.out.println("Vor TimerTask Erstellung");
			TimerTask timerTask = new TimerTaskThread(session);
			System.out.println("Vor Abfrage des Fragetexts");
			Question curQuestion = quiz.requestQuestion(ConnectionManager.getPlayer(session), timerTask, quizError);
			if(curQuestion != null)
			{
				System.out.println("Question Text:" + curQuestion.getQuestion());
			}
		}
		ConnectionManager.printall();
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
	
	private void sendPlayerlist() {
		JSONObject playerList = new JSONObject();
		playerList.put("Type", "6");
		playerList.put("Length", ConnectionManager.getSessionCount() * 37);
		Collection<Player> tmpPlayer = ConnectionManager.getPlayers();
		JSONArray players = new JSONArray();
		for(Player entry : tmpPlayer) {
			JSONObject tmp = new JSONObject();
			tmp.put("Spielername", entry.getName());
			System.out.println("Spieler: "+entry.getName());
			tmp.put("Punktestand", entry.getScore());
			tmp.put("ClientID", entry.getId());
			players.add(tmp);
		}
		playerList.put("Players", players);
		
		sendToAllSessions(playerList);
	}
}

var websocket;
var clientid = -1;
var numberOfPlayers = 0;
var gameStarted = false;
var curSelection = -2;
var queCell1;
var queCell2;
var queCell3;
var queCell4;
var firstRound = true;

function createWebsocket() {
	var uri = "ws://"+document.location.host+"/Aufgabe5/chat";
	websocket = new WebSocket(uri);
	websocket.onmessage = receivedMessage;

	websocket.onerror = function(evt) {
		console.log("onerror");
	}
	
	websocket.onclose = function() {
		console.log("onclose");
	}
	
	document.getElementById("buttonLogin").addEventListener("click", sendLoginRequest);
}

function sendLoginRequest() {
	
	var username = document.getElementById("input").value;
	var loginRequest = { 
			"Type" : "1",
			"Length" : username.length,
			"Name" : username
	}
	//stringify konvertiert einen JavaScript-Wert in eine JSON-Zeichenkette
	websocket.send(JSON.stringify(loginRequest));
	document.getElementById("input").value = "";
}

function checkIfEnoughPlayer() {
	if(numberOfPlayers > 1 && clientid == 0) {
		window.document.getElementById("buttonStart").disabled = false;
	}
}

function receivedMessage(message) {
	var msgFromServer = JSON.parse(message.data);
	console.log("Message empfangen: "+msgFromServer.Type);
	
	// LoginResponseOK
	if(msgFromServer.Type == 2) {
		// Speichern der Client ID, um den Spielleiter zu bestimmen
		clientid = msgFromServer.ClientID;
		if(clientid == 0)
		{
			document.getElementById("buttonStart").addEventListener("click", startGame);
		}
		
		//Login-Fenster entfernen
		if(clientid != 0) {
			var loginDiv = document.getElementById("login");
			loginDiv.parentNode.removeChild(loginDiv);
			var mainDiv = document.getElementById("main");
			var div = document.createElement("waitText");
			div.id = "waitText";
			var t = document.createTextNode("Warten auf Spielbeginn!");
			div.appendChild(t);
			mainDiv.appendChild(div);
		}
		else
		{
			document.getElementById("input").disabled = true;
			document.getElementById("buttonLogin").disabled = true;
		}
	}
	
	//CatalogChanged
	if(msgFromServer.Type == 5) {
		console.log("In Funktion 5");
		var filename = msgFromServer.Message;
		for(var i = 0; i < document.getElementsByClassName("catalog").length; i++)
		{
			console.log("In Loop 5");
			var catalog = document.getElementsByClassName("catalog")[i].textContent;
			console.log("catalogname: "+ catalog+ "filename: "+ filename);
			if(catalog==filename)
			{
				console.log("Gleicher Filename: "+filename);
				document.getElementsByClassName("catalog")[i].style.backgroundColor = "#f8a316";
			}
			else
			{
				document.getElementsByClassName("catalog")[i].style.backgroundColor = "white";
			}
		}
	}
	
	// PlayerList
	if(msgFromServer.Type == 6) {
		numberOfPlayers = msgFromServer.Length / 37;
		console.log("Anzahl Spieler: "+numberOfPlayers);
		if(gameStarted == false) {
			checkIfEnoughPlayer();
		}
		
		var playerTable = document.getElementById("playerTable");
		//playerTable.parentNode.removeChild(playerTable);
		console.log("Vorher: "+playerTable.rows.length);
		var flag = 0;
		if(playerTable.rows.length > 2)
		{
			flag = 1;
		}
		else if(playerTable.rows.length > 3)
		{
			flag = 2;
		}
		
		// Alle bisherigen Spieler löschen
		for(var i = 1; i < playerTable.rows.length; i++) {
			//var curRow = playerTable.rows[i];
			//if(curRow != undefined) {
			playerTable.deleteRow(i);
			//}
		}
		if(flag == 1)
		{
			playerTable.deleteRow(1);
		}
		if(flag == 2)
		{
			playerTable.deleteRow(2);
			playerTable.deleteRow(1);
		}

		console.log("Nachher: "+playerTable.rows.length);

		
		for(var i = 0; i < numberOfPlayers; i++) {			
			// Create an empty <tr> element and add it to the 2nd position of the table:
			var row = playerTable.insertRow(1);

			// Insert new cells (<td> elements) at the 1st and 2nd position of the "new" <tr> element:
			var cell1 = row.insertCell(0);
			var cell2 = row.insertCell(1);
			
			// Add text to the new cells:
			cell1.innerHTML = msgFromServer.Players[i].Spielername;
			cell2.innerHTML = msgFromServer.Players[i].Punktestand;
			console.log("Erzeuge Spieler: "+msgFromServer.Players[i].Spielername);
		}
	}
	
	if(msgFromServer.Type == 7) {
		console.log("In Funktion 7");
		var questionRequest = { 
				"Type" : "8",
				"Length" : "0"
		}
		websocket.send(JSON.stringify(questionRequest));
		console.log("QuestionRequest gesendet!");
	}
	
	if(msgFromServer.Type == 9) {
		console.log("In Funktion 9");
		if(msgFromServer.Length == 769)
		{
			if(firstRound == true) {
				if(clientid == 0) {
					var login = document.getElementById("login");
					login.parentElement.removeChild(login);
				}
				else {
					var waitText = document.getElementById("waitText");
					waitText.parentElement.removeChild(waitText);
				}
			}
			firstRound = false;
			//Neuen div für die Fragen anlegen und eine ID vergeben
			var questiondiv = document.createElement("questions");
			
			questiondiv.id = "questionwindow";
			
			if(document.getElementById("questionTable")!=null) {
				var table = document.getElementById("questionTable");
				table.parentElement.removeChild(table);
			}
			
			//Neue Tabelle anlegen und mit Reihen, Zellen und Werten füllen
			var queTable = document.createElement("table");
			queTable.id = "questionTable";
			var queRow0 = queTable.insertRow(0);
			var queRow1 = queTable.insertRow(1);
			var queRow2 = queTable.insertRow(2);
			
			var queCell0 = queRow0.insertCell(0);
			queCell0.setAttribute("colspan",2);

			queCell1 = queRow1.insertCell(0);
			queCell2 = queRow1.insertCell(1);
			queCell3 = queRow2.insertCell(0);
			queCell4 = queRow2.insertCell(1);
			
			queCell0.innerHTML = msgFromServer.Frage;
			
			queCell1.innerHTML = "A: "+msgFromServer.arrAnswer[0];
			queCell1.id = "0";
			queCell1.addEventListener("mouseover", mouseOverListener);
			queCell1.addEventListener("mouseout", mouseOutListener);
			queCell1.addEventListener("click", mouseClickListener);
			
			queCell2.innerHTML = "B: "+msgFromServer.arrAnswer[1];
			queCell2.id = "1";
			queCell2.addEventListener("mouseover", mouseOverListener);
			queCell2.addEventListener("mouseout", mouseOutListener);
			queCell2.addEventListener("click", mouseClickListener);
			
			queCell3.innerHTML = "C: "+msgFromServer.arrAnswer[2];
			queCell3.id = "2";
			queCell3.addEventListener("mouseover", mouseOverListener);
			queCell3.addEventListener("mouseout", mouseOutListener);
			queCell3.addEventListener("click", mouseClickListener);
			
			queCell4.innerHTML = "D: "+msgFromServer.arrAnswer[3];
			queCell4.id = "3";
			queCell4.addEventListener("mouseover", mouseOverListener);
			queCell4.addEventListener("mouseout", mouseOutListener);
			queCell4.addEventListener("click", mouseClickListener);
			
			questiondiv.appendChild(queTable);
			
			var position = document.getElementById("main");
			position.appendChild(questiondiv);
		}
		// Keine Frage mehr vorhanden!
		else {
			var endDiv = document.createElement("endDiv");
			var mainDiv = document.getElementById("main");
			questiondiv.id = "endDiv";
			
			var t = document.createTextNode("Alle Fragen beantwortet!");

			endDiv.appendChild(t);
			mainDiv.appendChild(endDiv);
		}
		
	}
	
	if(msgFromServer.Type == 11) {
		correctAnswer = msgFromServer.Correct;
		if(correctAnswer != -1) {
			var correctCell = document.getElementById(correctAnswer);
			correctCell.style.background = "green";
			if(curSelection != correctAnswer) {
				var falseCell = document.getElementById(curSelection);
				correctCell.style.background = "red";
			}
			
			// Entfernen der Listener
			queCell1.removeEventListener("mouseover", mouseOverListener);
			queCell1.removeEventListener("mouseout", mouseOutListener);
			queCell1.removeEventListener("click", mouseClickListener);
			
			queCell2.removeEventListener("mouseover", mouseOverListener);
			queCell2.removeEventListener("mouseout", mouseOutListener);
			queCell2.removeEventListener("click", mouseClickListener);
			
			queCell3.removeEventListener("mouseover", mouseOverListener);
			queCell3.removeEventListener("mouseout", mouseOutListener);
			queCell3.removeEventListener("click", mouseClickListener);
			
			queCell4.removeEventListener("mouseover", mouseOverListener);
			queCell4.removeEventListener("mouseout", mouseOutListener);
			queCell4.removeEventListener("click", mouseClickListener);
		}
		else {
			alert("Zeit abgelaufen!");
		}
		
		// 3 Sekunden warten -> die Hintergrundfarbe auf Standard
		setTimeout(function() {
			for(var i = 0; i < 4; i++) {
				var answers = document.getElementById(i);
				answers.style.backgroundColor = "white";
			}
			// eine neue Frage holen
			var questionRequest = {
					"Type": "8",
					"Length" : "0"
				};
			websocket.send(JSON.stringify(questionRequest));
		}, 3000);
	}
	
	if(msgFromServer.Type == 255) {
		console.log("Message: "+ msgFromServer.Message);
	}
}

function startGame() {
	if(curCat != null) {
		console.log("spiel wird gestartet");
		var startGame = { 
				"Type" : "7",
				"Length" : curCat.textContent.length,
				"Message" : curCat.textContent
		}
		websocket.send(JSON.stringify(startGame));
		console.log("Spielnachricht wurde versandt!");
		gameStarted = true;
	}
	else {
		alert("Kein Katalog ausgewählt!");
	}
}

//Antwortenlistener
function mouseClickListener(event) {
	event.target.style.background = "#f8a316";
	var questionAnswered = {
			"Type": "10",
			"Length" : "1",
			"Selection" : event.target.id
		};
	curSelection = event.target.id;
	
	console.log("Sende QuestionAnswered");
	websocket.send(JSON.stringify(questionAnswered));
}

function mouseOverListener(event) {
	event.target.style.background = "grey";
}

function mouseOutListener(event) {
	event.target.style.background = "white";
}

function sendCurCatalog() {
	console.log("Length: "+curCat.textContent);
	var changedCatalog = { 
			"Type" : "5",
			"Length" : curCat.textContent.length,
			"Message" : curCat.textContent
	}
	websocket.send(JSON.stringify(changedCatalog));
	console.log("aktueller Katalognachricht versendet!");
}
var websocket;
var clientid = -1;
var numberOfPlayers = 0;
var gameStarted = false;

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
	
	if(msgFromServer.Type == 255) {
		console.log("Message: "+ msgFromServer.Message);
	}
}

function startGame() {
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
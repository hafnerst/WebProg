var websocket;
var clientid = -1;
var numberOfPlayers = 0;
var gameStarted = false;

function createWebsocket() {
	var uri = "ws://"+document.location.host+"/Aufgabe5/chat";
	websocket = new WebSocket(uri);
	console.log("Create websocket");
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
	console.log("click");
}

function checkIfEnoughPlayer() {
	console.log("überprüfe");
	if(numberOfPlayers > 1) {
		console.log("aktiviere start button");
		window.document.getElementById("buttonStart").disabled = false;	
	}
}

function receivedMessage(message) {
	var msgFromServer = JSON.parse(message.data);
	console.log("Message empfangen: "+msgFromServer.Type);
	
	// LoginResponseOK
	if(msgFromServer.Type == 2) {
		// Speichern der Client ID, um den Spielleiter zu bestimmen
		clientID = msgFromServer.ClientID;
		numberOfPlayers++;
	}
	
	// PlayerList
	if(msgFromServer.Type == 6) {
		numberOfPlayers = msgFromServer.Length / 37;
		if(gameStarted == false) {
			checkIfEnoughPlayer();
		}
		
		var playerTable = document.getElementById("playerTable");

		// Alle bisherigen Spieler löschen
		for(var i = 1; i < 5; i++) {
			var curRow = playerTable.rows[i];
			if(curRow != undefined) {
				playerTable.deleteRow(i);
			}
		}
		
		for(var i = 0; i < numberOfPlayers; i++) {			
			// Create an empty <tr> element and add it to the 2nd position of the table:
			var row = playerTable.insertRow(1);

			// Insert new cells (<td> elements) at the 1st and 2nd position of the "new" <tr> element:
			var cell1 = row.insertCell(0);
			var cell2 = row.insertCell(1);
			
			// Add text to the new cells:
			cell1.innerHTML = msgFromServer.Players[i].Spielername;
			cell2.innerHTML = msgFromServer.Players[i].Punktestand;
		}
	}
}
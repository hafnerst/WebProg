var websocket;

function createWebsocket() {
	var uri = "ws://"+document.location.host+"/Aufgabe5/chat";
	websocket = new WebSocket(uri);
	console.log("Create websocket");
	websocket.onmessage = function(msg) {
		console.log("onmessage");
	}
	
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
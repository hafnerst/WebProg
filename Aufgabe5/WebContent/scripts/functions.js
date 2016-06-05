var text = "WebQuiz von Steffen und Daniel                                           ";
var count = 0;

window.onload = function () {
	window.setTimeout("lauf()",75);

	createWebsocket();
	getCatalog();
	console.log("in onLoad function()");
	
	document.getElementById("buttonStart").addEventListener("click", nextQuestion);
}

function lauf()
{
    text=text.slice(1,text.length)+text.slice(0,1);
    document.zeile.fenster.value=text;
    window.setTimeout("lauf()",75);
}

function addPlayer() {
	if(document.getElementById("input").value != "")
	{
		if(count < 4)
		{
			var table = document.getElementById("playerTable");
			var row = table.insertRow(1);
			var cell1 = row.insertCell(0);
			cell1.addEventListener("click", swapPlayers);
			var cell2 = row.insertCell(1);
			
			cell1.innerHTML = document.getElementById("input").value;
			var curId = count;
			cell1.id = curId.toString();
			cell2.innerHTML = "0";

			count++;
		}
		else
		{
			alert("Maximale Spielerzahl erreicht!");
		}
	}
	else
	{
		alert("Bitte Spielernamen eingeben!");
	}
	if(count > 1)
	{
		document.getElementById("buttonStart").disabled=false;
	}
}

function nextQuestion() {	
	var login = document.getElementById("login");
	login.parentElement.removeChild(login);
	
	//Neuen div für die Fragen anlegen und eine ID vergeben
	var questiondiv = document.createElement("div");
	questiondiv.id = "questionwindow";
	
	//Neue Tabelle anlegen und mit Reihen, Zellen und Werten füllen
	var queTable = document.createElement("table");
	var queRow0 = queTable.insertRow(0);
	var queRow1 = queTable.insertRow(1);
	var queRow2 = queTable.insertRow(2);
	
	var queCell0 = queRow0.insertCell(0);
	queCell0.setAttribute("colspan",2);

	var queCell1 = queRow1.insertCell(0);
	var queCell2 = queRow1.insertCell(1);
	var queCell3 = queRow2.insertCell(0);
	var queCell4 = queRow2.insertCell(1);
	
	queCell0.innerHTML = "Die Quadratwurzel von 100 ist:";
	queCell1.innerHTML = "A: 10";
	queCell2.innerHTML = "B: 2.76";
	queCell3.innerHTML = "C: 5";
	queCell4.innerHTML = "D: 1";
	
	questiondiv.appendChild(queTable);
	
	var position = document.getElementById("main");
	position.appendChild(questiondiv);
}

function swapPlayers(event) {
	if(event.target.id < (count-1))
	{
		var buf = event.target.innerHTML;
		var curID = count-1;
		event.target.innerHTML = document.getElementById(curID.toString()).innerHTML;
		document.getElementById(curID.toString()).innerHTML = buf;
	}
}
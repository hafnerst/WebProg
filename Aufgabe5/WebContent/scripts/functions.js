var text = "WebQuiz von Steffen und Daniel                                           ";
var count = 0;

window.onload = function () {
	window.setTimeout("lauf()",75);

	createWebsocket();
	getCatalog();
	console.log("in onLoad function()");
	document.getElementById("buttonLogin").disabled = false;
	document.getElementById("input").disabled = false;
	document.getElementById("buttonStart").disabled = true;
}

function lauf()
{
    text=text.slice(1,text.length)+text.slice(0,1);
    document.zeile.fenster.value=text;
    window.setTimeout("lauf()",75);
}
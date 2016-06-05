var request;
var READY = 4;
var curCat = null;
function getCatalog(){
	request = new XMLHttpRequest();
	request.onreadystatechange = function() {
	    if (request.readyState == 4 && request.status == 200) {
	        catalogListener();
	    }
	};
	request.open("POST", "catalogList", true);
	request.send();
	console.log("Request gesendet");
}

function catalogListener(){
	
	if(request.readyState == READY){
		
		var xmlDoc = request.responseXML;
		var count = 0;
		for(var i = 0; i < (xmlDoc.childNodes.length +1); i++)
		{
			var catalog = xmlDoc.getElementsByTagName("name")[count];
			var catValue = catalog.firstChild.nodeValue;
			console.log(catValue);
			
		    var div = document.createElement("div");
		    div.innerHTML = '<label class="catalog" onclick="changeCatalog(this)">'+catValue+'</label>';
		    document.getElementById("catalogs").appendChild(div.childNodes[0]);
		    div.innerHTML = '<br></br>'
		    document.getElementById("catalogs").appendChild(div.childNodes[0]);
		    
		    count++;
		}
		
	}
	console.log("in catalogListener");
}

function receiveCatalog(message){
	
	var elements = document.getElementsByClassName("catalog");
    var length = elements.length;
   
    for(var i = 0; i < length; i++)
    {
    	elements[i].style.backgroundColor = "";
    	if(elements[i].innerHTML == message){
    		elements[i].style.backgroundColor = "green";
    	}
    }
}

function changeCatalog(selectedCat){
	
	selectedCat.style.backgroundColor = "#f8a316";
	
	if(curCat!=null)
	{
		curCat.style.backgroundColor = "white";
	}
	curCat = selectedCat;
}
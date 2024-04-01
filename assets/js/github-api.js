var apiRoot = "https://api.github.com/";

window.onload = function(e){ 
    console.log(window.location.pathname)
    if ( window.location.pathname.match("index") != null || window.location.pathname == "/" || window.location.pathname == "" || window.location.pathname.match("MeteorPlus")) {
        var total = 0;
        fetch(apiRoot + "repos/" + "NekiPlay" + "/" + "MeteorPlus" +        "/releases", {
            method: "GET"
        })
        .then(x => x.json())
        .then(data => 
        {
            var json = data
            for (var info of json) 
            {
                var assets = info["assets"]
                for (var asset of assets) 
                {
                    total += asset.download_count;
                }
            }
            document.getElementsByClassName("mb-0")[0].innerText = total
        })
    
        // Stars
        fetch(apiRoot + "repos/" + "NekiPlay" + "/" + "MeteorPlus", {
            method: "GET"
        })
        .then(x => x.json())
        .then(data => 
        {
            var json = data;
            document.getElementsByClassName("mb-0")[2].innerText = json["stargazers_count"]
        })
    } 
}

document.getElementById("clickMe").onclick = function () {
	
	let headers = new Headers();
	
	headers.append('Content-Type', 'application/json');
	headers.append('Accept', 'application/json');
	
	headers.append('Access-Control-Allow-Origin', '*');
	headers.append('Referer', 'https://meteor-plus.com/');
	
	headers.append('GET', 'POST', 'OPTIONS');


	
	fetch("https://lk.rukassa.is/api/v1/create?shop_id=2100&order_id=1&amount=1&token=907a67fe804b9b42a418828d597f5de5&currency=USD", {
        method: "GET",
		headers: headers
    })
	.then(x => x.json())
    .then(data => 
    {
        var json = data
		console.log(json)
        window.open(json.url,"_self")
    })
};
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
        })
		document.getElementById("downloads").innerText = total;
    
        // Stars
        fetch(apiRoot + "repos/" + "NekiPlay" + "/" + "MeteorPlus", {
            method: "GET"
        })
        .then(x => x.json())
        .then(data => 
        {
            var json = data;
            document.getElementById("stars").innerText = json["stargazers_count"]
        })
    } 
}
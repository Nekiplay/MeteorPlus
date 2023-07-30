var apiRoot = "https://api.github.com/";

window.onload = function(e){ 
    var total = 0;
    fetch(apiRoot + "repos/" + "NekiPlay" + "/" + "MeteorPlus" + "/releases", {
        method: "GET"
    })
    .then(x => x.json())
    .then(data => {
        var json = data
        for (var info of json) 
        {
            var assets = info["assets"]
            for (var asset of assets) 
            {
                total += asset.download_count;
                console.log(asset.download_count);
            }
        }
        document.getElementsByClassName("mb-0")[0].innerText = total
         console.log(total);
     })
    
    // Stars
    fetch(apiRoot + "repos/" + "NekiPlay" + "/" + "MeteorPlus", {
        method: "GET"
    })
    .then(x => x.json())
    .then(data => {
        var json = data;
        document.getElementsByClassName("mb-0")[2].innerText = json["stargazers_count"]
         console.log(total);
     })
    
}
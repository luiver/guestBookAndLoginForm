// alert("Hello!")
let button = document.querySelectorAll("a");

button.forEach(b => b.addEventListener("click", function(){
    // let newPath = getInput("delete");
    // let myVar = setInterval(myTimer, 100);
    // clearInterval(myVar);
    let newPath = "/guestbook";
    updateUrlPath("", "NewTitle", newPath);

}))

function updateUrlPath(stateObject, title, path){
    window.history.replaceState(stateObject, title, path);
}

function myTimer() {
    let newPath = "/guestbook";
    updateUrlPath("", "NewTitle", newPath);
}
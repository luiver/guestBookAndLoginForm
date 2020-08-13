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

let user1 = { 'username': "rafcio", 'password': "qwe" };
let user2 = { 'username': "xxx", 'password': "xxx" };
let user3 = { 'username': "1", 'password': "1" };

// Put the object into storage
localStorage.setItem('user1', JSON.stringify(user1));
localStorage.setItem('user1', JSON.stringify(user2));
localStorage.setItem('user1', JSON.stringify(user3));
// Retrieve the object from storage
//let retrievedObject = localStorage.getItem('testObject');

//console.log('retrievedObject: ', JSON.parse(retrievedObject));
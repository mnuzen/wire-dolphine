/*
No easy way to pass string from servlet it HTML
JS gets users API login/logout url and the username of current user
/account_info format is URL, Username
*/

function getLoginURL() {
  fetch('/account_info').then(response => response.text()).then((message) => {
  var user = eval(message); 
 
  //get URL and hide uneeded elements
  if(user[1] == "NULL"){
    document.getElementById("loginDiv").classList.remove("hidden");
    document.getElementById("userDiv").classList.add("hidden");
    document.getElementById("loginURL").setAttribute('href', user[0]);
  }
  else{
    document.getElementById("loginDiv").classList.add("hidden");
    document.getElementById("userDiv").classList.remove("hidden");
    document.getElementById("logoutURL").setAttribute('href', user[0]);
    document.getElementById("userName").innerHTML = user[1];
  }

});
}
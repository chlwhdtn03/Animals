var socket;
var player_count = 0; // 접속했었던 플레이어 수
var joined = new Array(); // 자신을 제외한 나머지 플레이어들 객체
var my; // 자신 객체

var isStarted = 0 // 게임이 시작했는지

var disconnectCode = 0;


function notice(msg) {
    // Get the snackbar DIV
    var x = document.getElementById("snackbar");
    x.innerText = msg;
  
    // Add the "show" class to DIV
    x.className = "show";
  
    // After 3 seconds, remove the show class from DIV
    setTimeout(function(){ x.className = x.className.replace("show", ""); }, 5000);
}

function send(obj) {
    socket.send(obj);
}

function ready() {
    if(my.ready == false) {
        var packet = new Packet("ready", my.name);
        socket.send(JSON.stringify(packet).trim());
    }
}

function joinGame() {
    $("#intro-form").remove();
    socket = new WebSocket("ws://" + location.host + "/animals");

    socket.onopen = function() { // 서버 접속됐을 때
        // 서버에게 내 정보 전송
        var myPacket = new Packet("my",my);
        
        socket.send(JSON.stringify(myPacket).trim());
    }
    
    socket.onclose = function() { // 연결 끊어졌을때
        switch(disconnectCode) {
            case 0:
                notice("서버와의 연결이 끊겼습니다.");
                break;
            case 1:
                notice("중복된 닉네임을 사용했습니다. F5를 눌러 다시 접속하세요.")
        }
    }
    
    socket.onmessage = function(a) { // 서버한테 메세지 받을 때
        var type = JSON.parse(a.data).type;
        var data = JSON.parse(JSON.parse(a.data).data);
        console.log(data);
        switch(type) {
            
            case "kick":
                disconnectCode = data;
                break;
                
            case "chat":
                appendChat(data);
                break;
        
            case "build":
                $("#buildspan").text("Build. " + data);
                break;

            case "ready":
                getPlayer(data.readyer).ready = true;
                checkReady(data.readyer);
                break;
                
            case "join":
                addPlayer(data);
                addPlayerbox(data.name);
                checkReady(data.name);
                break;

            case "leave":
                removePlayer(data);
                deletePlayerbox(data.name);
                break;
                
            case "changeProfile":
                getPlayer(data.name).animal = data.animal;
                getPlayerboxImage(data.name).getElementsByTagName("img")[0].src = "resource/entity/"+data.animal+".png";
                break;
        }
    }

}

function addPlayer(player)  { // 플레이어 추가
    if(player.name == my.name) {
        joined.push(my);
    } else {
        joined.push(player);
    }
}

function removePlayer(player)  { // 플레이어 제거
    const itemToFind = joined.find(function(item) {
        return item.name == player.name;
    });
    const index = joined.indexOf(itemToFind);
    if(index > -1)
        joined.splice(index, 1)
}

function checkReady(name) {
    if(getPlayer(name).ready) {
        getPlayerbox(name).style.backgroundColor = '#F29494';
        getPlayerboxImage(name).getElementsByTagName("img")[0].style.backgroundColor = '#f294aa';
    } else {
        getPlayerbox(name).style.backgroundColor = '#148BA6';
        getPlayerboxImage(name).getElementsByTagName("img")[0].style.backgroundColor = '#148BB9';
    }
}

function getPlayer(pname)  { // 플레이어 객체 가져오기
    return joined.find(function(item) {
        return item.name == pname;
    });
}


function checkAllPlayer()  { // 레디한 플레이어 설정
    console.log(joined);
}

function addPlayerbox(name) {
    /**
     * <div id="playerbox">
        <img id="playerphoto" width="120px" height="120px">
        <figcaption id="playername">블랙핑크</figcaption>
        </div>
     */

     var rootdiv = document.createElement("div");
     rootdiv.setAttribute("id", "playerbox");
     rootdiv.setAttribute("owner", name);

     var imgtag = document.createElement("img");
     imgtag.setAttribute("id", "playerphoto");
     imgtag.setAttribute("width", "120px");
     imgtag.setAttribute("height", "120px");

     var nametag = document.createElement("figcaption");
     nametag.setAttribute("id","playername");
     nametag.innerText = name;

     rootdiv.appendChild(imgtag);
     rootdiv.appendChild(nametag);
    
     $("#Queue").append(rootdiv);
}

function getPlayerbox(name) {
    var rootdiv = document.getElementById("Queue");
    for(var i = 0; i < rootdiv.childElementCount; i++) {
        var adiv = rootdiv.childNodes[i];
        if(adiv.nextSibling.getAttribute("owner") == name) {
            return adiv.nextSibling;
        }
    }
}

function getPlayerboxImage(name) {
    var rootdiv = document.getElementById("Queue");
    for(var i = 0; i < rootdiv.childElementCount; i++) {
        var adiv = rootdiv.childNodes[i];
        if(adiv.nextSibling.getAttribute("owner") == name) {
            return adiv.nextElementSibling;
        }
    }
}

function deletePlayerbox(name) {
    var rootdiv = document.getElementById("Queue");
    console.log(rootdiv.childElementCount);
    for(var i = 0; i < rootdiv.childElementCount; i++) {
        var adiv = rootdiv.childNodes[i];
        console.log(adiv);
        if(adiv.nextSibling.getAttribute("owner") == name) {
            rootdiv.removeChild(adiv.nextSibling);
            return;
        }
    }
}

function appendChat(chat) {
    var rootp = document.createElement("span");
    rootp.style.display = "block";
    rootp.innerText = chat.name + " > " + chat.message;
    var chatdiv = document.getElementById("footer-chat");
    chatdiv.appendChild(rootp);
    chatdiv.scrollTop = chatdiv.scrollHeight;
}

$(function() {

    $("#intro").submit(function(event) {
        event.preventDefault();
        var userName = $("#intro-name").val().trim();
        if(userName.trim() == "")
            return;
        console.log(userName);
        my = new Player(userName, 0, 0);
        joinGame();
    });

    $("#chatting").submit(function(event) {
        event.preventDefault();
        var message = $("#input-chat").val().trim();
        if(message.trim() == "")
            return;
        socket.send(JSON.stringify(new Packet("chat", new Chat(my.name, message))));
        $("#input-chat").val("");
    });

});

function Packet(type, data) {
    this.type = type;
    this.data = data;
}

function Player(name, x, y) { // 플레이어 객체
    this.name = name;
    this.x = x;
    this.y = y;
    this.animal = ""; // 어떤 동물인지
    this.ready = false; // 준비했는지 여부  
    this.leaved = false; // 나갔는지 여부
}

function Chat(name, message) { // 채팅 객체
    this.name = name;
    this.message = message; // 나갔는지 여부
}

function findPlayer (name) { // 닉네임을 가지고 joined배열에서 플레이어 객체 찾기 (닉네임이 자신일 경우 myUser 반환)
    if (name == myUser.name)
        return myUser;
    for (var i = 0; i < id; i++) {
        if (joined[i].leaved == false) {
            if (joined[i].name == name) {
                return joined[i];
            }
        }
    }
}
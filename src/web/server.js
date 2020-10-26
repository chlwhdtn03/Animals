var socket;
var player_count = 0; // 접속했었던 플레이어 수
var joined = new Array(); // 자신을 제외한 나머지 플레이어들 객체
var my; // 자신 객체

var isStarted = 0 // 게임이 시작했는지


function send(obj) {
    socket.send(obj);
}

function joinGame() {
    $("#intro-form").remove();
    socket = new WebSocket("ws://" + location.host + "/animals");

    socket.onopen = function() { // 서버 접속됐을 때
        alert("접속 성공");
        // 서버에게 내 정보 전송
        var myPacket = new Packet("my",my);
        socket.send(JSON.stringify(myPacket));
    }
    
    socket.onclose = function() { // 연결 끊어졌을때
        alert("접속 해제");
    }
    
    socket.onmessage = function(a) { // 서버한테 메세지 받을 때
        alert(a.data);
    }

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

});

function Packet(type, data) {
    this.type = type;
    this.data = data;
}

function Player(name, x, y) { // 플레이어 객체
    this.name = name;
    this.x = x;
    this.y = y;
    this.leaved = false; // 나갔는지 여부
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
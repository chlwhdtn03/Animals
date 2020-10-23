socket = new WebSocket("ws://" + location.host + "/animals");

socket.onopen = function() { // 서버 접속됐을 때
    alert("접속 성공");
}

socket.onclose = function() { // 연결 끊어졌을때
    alert("접속 해제");
}

socket.onmessage = function(a) {
    alert(a.data);
}

function send(obj) {
    socket.send(obj);
}
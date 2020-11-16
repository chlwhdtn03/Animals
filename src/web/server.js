var socket;
var player_count = 0; // 접속했었던 플레이어 수
var joined = new Array(); // 자신을 제외한 나머지 플레이어들 객체
var my; // 자신 객체

var nowPressed = new Array();

var isStarted = false // 게임이 시작했는지

var disconnectCode = 0;

var noticeThread;

var camera = new Camera(0,0);

const VK_W = 87;
const VK_A = 65;
const VK_S = 83;
const VK_D = 68;
const VK_SPACE = 32;

var mapfont_size = 0;

var ticks, frames_count, delta = 0, tick_rate = 1000 / 60
function notice(msg) {
    // Get the snackbar DIV
    var x = document.getElementById("snackbar");
    x.innerHTML = msg;
  
    // Add the "show" class to DIV
    if(x.className != "show")
        x.className = "show";
  
    // After 5 seconds, remove the show class from DIV
    clearTimeout(noticeThread)
    noticeThread = setTimeout(function(){ x.className = x.className.replace("show", "hide"); }, 5000);
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
                notice("중복된 닉네임을 사용했습니다.<br>F5를 눌러 다시 접속하세요.")
                break;      
            case 2:
                notice("<span style='color:red;'>뭐해?</span>")
                break;
        }
    }
    
    socket.onmessage = function(a) { // 서버한테 메세지 받을 때
        var type = JSON.parse(a.data).type;
        var data = JSON.parse(JSON.parse(a.data).data);
        switch(type) {
                
            case "waitTostart":
                notice(data + "초 후 게임이 시작됩니다!")
                break;
                
            case "startgame":
                isStarted = true;
                console.log("Game Started!")
                notice("<b style='color:yellow;'>게임 시작!</b>")
                InGame();
                break;
                
            case "started":
                isStarted = data;
                console.log("isStarted " + isStarted)
                if(isStarted)
                    InGame();
                break;
                
            
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
                
            case "move":
                getPlayer(data.name).x = data.x;
                getPlayer(data.name).y = data.y;
                getPlayer(data.name).direction = data.direction;
                break;
        }
    }

}

function InGame() {
    $("#QueueFrame").hide();
    $("#InGameFrame").show();

    var canvas = document.getElementById("InGameCanvas");
   
    
    $("#InGameFrame").focus();
    var ctx = canvas.getContext("2d", {alpha: false});
    
    ctx.canvas.width  = 1920;
  	ctx.canvas.height = window.innerHeight;
  	
  	
    mapfont_size = 200 / 10
    
    var isObserver = my.animal == "";
    if(isObserver) {
    	notice("관전")
    }
    
    var dx=0, dy=0;
    var lastTimer = performance.now()
    temp = performance.now()
    function loop(timestamp) {
		
		delta += (timestamp - temp) / tick_rate;
		temp = timestamp
		var shouldRender = true;
		
		dx = 0;
		dy = 0;
		
	    if(nowPressed.includes(VK_W)) {
	       	if(!isObserver) { // 플레이어
		       	dy = -2;
		      	
		       	if(my.y > 0) {
		       		my.y -= 2
		       	}
		        if(camera.y > 0 && my.centerY < MAP_FIELD.height-(canvas.height/2)) {
		            camera.y -= 2
			    }
		    } else { // 관전자
		        
		      	if(camera.y > 0) {
		       		camera.y -= 4;
		       	}
		        
		    }
	            
	    }
	
	        if(nowPressed.includes(VK_S)) {
	        	if(!isObserver) { // 플레이어
		            dy = +2;
		            if(my.y < MAP_FIELD.height-150)
		            	my.y += 2;
		            if(my.centerY > canvas.height/2 && camera.y+canvas.height < MAP_FIELD.height)
		            	camera.y += 2;
	            } else { // 관전자
		            if(camera.y + canvas.height < MAP_FIELD.height)
			            camera.y += 1*4;
	            }
	        }
	
	        if(nowPressed.includes(VK_A)) {
	        	if(!isObserver) { // 플레이어
		        	dx = -2;
		       		if(my.x > 0)
		       			my.x -= 2;
		            
		            if(camera.x > 0 && my.centerX < MAP_FIELD.width-(canvas.width/2))
		                camera.x -= 2;  
	            } else { // 관전자
	           		if(camera.x > 0)
		                camera.x -= 1*4;
	            }
	                
	        }
	
	        if(nowPressed.includes(VK_D)) {
	        	if(!isObserver) {
		        	dx = +2;
		        	if(my.x < MAP_FIELD.width-150)
		            	my.x +=2
		            if(my.centerX > canvas.width/2 && camera.x+canvas.width < MAP_FIELD.width)
		            	camera.x += 2
	            } else {
	            	if(camera.x + canvas.width < MAP_FIELD.width)
	            		camera.x += 1*4;
	            }
	        }
	        
	        if(!isObserver) {
		        my.centerX = my.x + 150/2;
		        my.centerY = my.y + 150/2;
	        }
    
        // INPUT
        while(delta >= 1) {
            ticks++;
            
	       	if((dx || dy) && !isObserver) {
	       		if(dx < 0)
	            	my.direction = "left";
	            if(dx > 0)
	            	my.direction = "right"
	            		
	        	socket.send(JSON.stringify(new Packet("move", my)));
	        	
	       	}
	        delta -= 1;
	        shouldRender = true;
			
	    }

        // GRAPHIC

		if(shouldRender) {
			frames_count++
	        ctx.clearRect(0, 0, canvas.width, canvas.height);

	        ctx.drawImage(MAP_FIELD, camera.x, camera.y, (camera.x+canvas.width), (camera.y+canvas.height), 0, 0, camera.x+canvas.width, camera.y+canvas.height);
	  		
			// 캐릭터 그리기
			for(var p of joined) {
				if(p.name == my.name) {
					
					// 내 캐릭터 그리기 || 만약 지금 내가 캐릭터가 있는가를 구분하여 관전자인지 플레이어로 구분
			  		if(!isObserver) {
				  		if(my.direction == "right") { // 오른쪽으로 간다면( dx가 양수일 때)
				  			ctx.save();
				        	ctx.drawImage(ENTITY_HORSE, 
				        		my.centerX < canvas.width/2 ?
				        			 my.x : (camera.x+canvas.width < MAP_FIELD.width) ?
				        			  (canvas.width/2)-150/2 : my.x-camera.x,
				        		my.centerY < (canvas.height/2) ?
				        			 my.y : (camera.y+canvas.height < MAP_FIELD.height) ?
				        			  (canvas.height/2)-150/2 : my.y-camera.y,
				        		150, 150);
				        	ctx.restore();
				        } else if(my.direction == "left") { // 왼쪽으로 간다면( dx가 음수일 때)
				        	ctx.save();
				        	ctx.scale(-1,1);
				        	ctx.drawImage(ENTITY_HORSE,
				        		 my.centerX < canvas.width/2 ?
				        		 	 -(my.x)-150 : (camera.x+canvas.width < MAP_FIELD.width) ?
				        		 	 	 -(canvas.width/2)-150/2 : -(my.x-camera.x)-150,
				        		 my.centerY < canvas.height/2 ?
				        		 	my.y : (camera.y+canvas.height < MAP_FIELD.height) ?
				        		 		(canvas.height/2)-150/2 : (my.y-camera.y),
				        		 150, 150);
				        	ctx.restore(); 
						}
					}
					
					continue; // 내꺼 다 그렸으면 continue해서 다른얘 그리러
				} else if(p.animal != "") { // 관전 모드인지 확인. 관전 아니면 그려주기
					if(p.x < camera.x-150 || p.x > camera.x+canvas.width)
						continue;
					if(p.y < camera.y-150 || p.y > camera.y+canvas.height)
						continue;
					if(p.direction == "right") {
						ctx.drawImage(ENTITY_HORSE, p.x-camera.x, p.y-camera.y, 150, 150)
					} else if(p.direction == "left") {

			        	ctx.save();
			        	ctx.scale(-1,1);
						ctx.drawImage(ENTITY_HORSE, -(p.x-camera.x)-150, p.y-camera.y, 150, 150)
			        	ctx.restore(); 
					}
				}
			}
			
			
			
			
			ctx.drawImage(map(ctx),50,50)
			
		}
		
		if(timestamp - lastTimer >= 1000) {
			lastTimer += 1000
			console.log(ticks + " Ticks," + frames_count + " frames");
			frames_count = 0;
			ticks = 0;
		}
		
    	if(isStarted)
    		requestAnimationFrame(loop);
    		
		
    }
    window.requestAnimationFrame(loop);

    
    
}
var temp = 0;

function map(ctx){
	var c=document.createElement('canvas');
	var c2=c.getContext('2d');
	
	//c2.canvas.width =  MAP_FIELD.width
	//c2.canvas.height = MAP_FIELD.height
	c2.canvas.width = 200
	c2.canvas.height= Math.floor(200*MAP_FIELD.height/MAP_FIELD.width)
	
	var scale = 200 / MAP_FIELD.width
	
	c2.globalAlpha = 0.6; // 맵 투명도
	
	c2.drawImage(MAP_FIELD,0,0,c.width,c.height);
	
	c2.strokeStyle = 'darkgreen'
	c2.lineWidth = 2
	c2.strokeRect(camera.x*scale, camera.y*scale, ctx.canvas.width*scale, ctx.canvas.height*scale);
	
	c2.strokeStyle = 'white' 
	c2.lineWidth = 5
	c2.strokeRect(0, 0, c2.canvas.width, c2.canvas.height);
	
	
	if(my.animal != "") { // 내가 관전자가 아닌 플레이어일 경우 지도에 위치 표시	
		c2.beginPath()
		c2.lineWidth = 2
		c2.strokeStyle = 'red'
		c2.arc(my.centerX*scale, my.centerY*scale, 2, 0, 2 * Math.PI);
		c2.fill()
		c2.closePath()
	} else { // 관전자일 경우
		c2.font = mapfont_size + "px bold";
		c2.fillText("관전 모드", 5,c.height-mapfont_size/5)
	}
	
	return(c);
}

function BackToQueue() {
    $("#QueueFrame").show();
    $("#InGameFrame").hide();   
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
    
    // My Ready Button
    if(name == my.name) {
        if(my.ready) {
            $("#btn-ready").css("background-color", "green");
            $("#btn-ready").html("준비완료!");
        } else {
            $("#btn-ready").css("background-color", "steelblue");
            $("#btn-ready").html("준비하기");
        }
    }
        

    // PlayerBox
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
    rootp.innerText = "<"+ chat.name + "> " + chat.message;
    var chatdiv = document.getElementById("footer-chat");
    chatdiv.appendChild(rootp);
    chatdiv.scrollTop = chatdiv.scrollHeight;
}

$(function() {

    $("#intro-name").focus();

    $("#InGameFrame").keydown(function (event) {
        if(nowPressed.includes(event.keyCode) == false) {
            nowPressed.push(event.keyCode)
        }
    });

    $("#InGameFrame").keyup(function (event) {
        if(nowPressed.includes(event.keyCode) == true) {
            const idx = nowPressed.indexOf(event.keyCode);
            nowPressed.splice(idx, 1)
        }
    });

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

function Camera(x, y) {
    this.x = x;
    this.y = y;
}

function Packet(type, data) {
    this.type = type;
    this.data = data;
}

function Player(name, x, y) { // 플레이어 객체
    this.name = name;
    this.x = x;
    this.y = y;
    this.direction = ""; // 어느 방향 쳐다보는지
    this.centerX = 0;
    this.centerY = 0;
    this.animal = ""; // 어떤 동물인지
    this.ready = false; // 준비했는지 여부  
    this.leaved = false; // 나갔는지 여부
}

function Chat(name, message) { // 채팅 객체
    this.name = name;
    this.message = message; // 나갔는지 여부
}


// INIT IMAGE

var MAP_FIELD = new Image();
var MAP_DESERT = new Image();
var MAP_SNOW = new Image();

MAP_FIELD.src = "./map/desert.png"
MAP_DESERT.src = "./map/desert.png"
MAP_SNOW.src = "./map/ice.png"

// ENTITY IMAGE

var ENTITY_CHITA = new Image();
var ENTITY_HORSE = new Image();
var ENTITY_CROCKDAIL = new Image();
var ENTITY_COLORED_HORSE = new Image();
var ENTITY_HAMA = new Image();
var ENTITY_NORU_RED = new Image();
var ENTITY_NORU = new Image();
var ENTITY_RION = new Image();
var ENTITY_SMART_MONKEY = new Image();

ENTITY_CHITA.src = "./resource/entity/chita.png"
ENTITY_HORSE.src = "./resource/entity/horse.png"
ENTITY_CROCKDAIL.src = "./resource/entity/crockdail.png"
ENTITY_COLORED_HORSE.src="./resource/entity/colored_horse.png"
ENTITY_HAMA.src="./resource/entity/hama.png"
ENTITY_NORU_RED.src="./resource/entity/noru_red.png"
ENTITY_NORU.src="./resource/entity/noru.png"
ENTITY_RION.src="./resource/entity/rion.png"
ENTITY_SMART_MONKEY.src="./resource/entity/smart_monkey.png"


// ITEM IMAGE
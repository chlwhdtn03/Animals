package server;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import data.Player;
import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import packet.AnimalsPacket;
import util.Log;

public class ConnectionListener implements Handler<ServerWebSocket> {

	List<Player> onlinePlayers = new ArrayList<Player>();
	
	@Override
	public void handle(ServerWebSocket ws) {
		
		ws.frameHandler(frame -> { // 한 클라이언트가 메세지를 보낼 때

			Gson gson = new Gson();
			Log.info(frame.textData());
			AnimalsPacket packet = gson.fromJson(frame.textData(), AnimalsPacket.class);
			
			switch(packet.getType()) { // server.js 참고
			case "my":
				
				Player player = gson.fromJson(packet.getData().toString(), Player.class);
				player.setWs(ws);
				onlinePlayers.add(player);
				Log.info(player.getName() + "(" + ws.remoteAddress() + ") 가 접속했습니다.");
				
				break;
			}
			
		});
		
		ws.closeHandler(v -> { // 한 클라이언트의 접속 끊겼을때
			
		});
		
	}
	
	/**
	 * @see 접속자 모두에게 데이터 전송
	 * @param msg 전송할 내용
	 */
	public void sendAll(AnimalsPacket packet) {
		
		for(Player p : onlinePlayers) {
			p.getWs().writeFinalTextFrame(packet.toString());
		}
	}

}

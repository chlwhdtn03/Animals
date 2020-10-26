package server;

import com.google.gson.Gson;

import data.Player;
import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import packet.AnimalsPacket;
import util.Log;

public class ConnectionListener implements Handler<ServerWebSocket> {

	@Override
	public void handle(ServerWebSocket ws) {
		
		ws.frameHandler(frame -> { // 한 클라이언트가 메세지를 보낼 때
			Gson gson = new Gson();
			Log.info(frame.textData());
			AnimalsPacket packet = gson.fromJson(frame.textData(), AnimalsPacket.class);
			Player player = gson.fromJson(packet.getData().toString(), Player.class);
			try {
			Log.info(player.getName() + " 입니다.");
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
		
		ws.closeHandler(v -> { // 한 클라이언트의 접속 끊겼을때
			
		});
		
	}

}

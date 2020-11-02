package server;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import animals.Animals;
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
//			Log.info(frame.textData());
			AnimalsPacket packet = gson.fromJson(frame.textData(), AnimalsPacket.class);

			switch (packet.getType()) { // server.js 참고
			case "my":
				try {
					Player player = gson.fromJson(gson.toJson(packet.getData()), Player.class);
					player.setWs(ws);
					
					send(ws, new AnimalsPacket("build", Animals.build));					
					for (Player p : Animals.onlinePlayers) {
						send(ws, new AnimalsPacket("join", p)); // 지금 접속한 플레이어에게 모든 플레이어 정보 전송
					}

					Animals.onlinePlayers.add(player); // 지금 접속한 플레이어 저장

					sendAll(new AnimalsPacket("join", player)); // 모든 플레이어에게 현재 접속한 플레이어 정보 전송
					Log.info(player.getName() + "(" + ws.remoteAddress() + ") 가 접속했습니다.");
					Animals.gui.refreshPlayerList();
				} catch (Exception e) {
					Log.error(e);
				}
				break;
			}

		});

		ws.closeHandler(v -> { // 한 클라이언트의 접속 끊겼을때
			if (isOnline(ws)) {
				Player player = getPlayer(ws);
				Log.info(player.getName() + "(" + ws.remoteAddress() + ") 가 나갔습니다.");
				Animals.onlinePlayers.remove(player);
				sendAll(new AnimalsPacket("leave", player));
				Animals.gui.refreshPlayerList();

			}
		});

	}

	/**
	 * @see 접속자 모두에게 데이터 전송
	 * @param msg 전송할 내용
	 */
	public void sendAll(AnimalsPacket packet) {

		for (Player p : Animals.onlinePlayers) {
			try {
				p.getWs().writeFinalTextFrame(packet.toString());
			} catch (Exception e) {
				Log.error(e);
			}
		}
	}

	public void send(ServerWebSocket ws, AnimalsPacket packet) {
		try {
			ws.writeFinalTextFrame(packet.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isOnline(ServerWebSocket ws) {
		return Animals.onlinePlayers.stream().anyMatch(obj -> obj.getWs().equals(ws));
	}

	public Player getPlayer(ServerWebSocket ws) {
		return Animals.onlinePlayers.stream().filter(obj -> obj.getWs().equals(ws)).findFirst().get();
	}

}

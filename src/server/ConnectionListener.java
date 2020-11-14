package server;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;

import animals.AnimalType;
import animals.Animals;
import data.Chat;
import data.Player;
import data.Ready;
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
					
					if(isAlreadyName(player.getName())) {
						Log.warning(ws.remoteAddress() + "에서 중복된 닉네임("+player.getName()+"을 사용하여 연결 해제하였습니다.");
						send(ws, new AnimalsPacket("kick", 1));
						ws.close();
						return;
					}
					
					player.setWs(ws);
					
					send(ws, new AnimalsPacket("build", Animals.build));
					send(ws, new AnimalsPacket("started", Animals.isStarted));
					for (Player p : Animals.onlinePlayers) {
						send(ws, new AnimalsPacket("join", p)); // 지금 접속한 플레이어에게 모든 플레이어 정보 전송
					}

					Animals.onlinePlayers.add(player); // 지금 접속한 플레이어 저장

					sendAll(new AnimalsPacket("join", player)); // 모든 플레이어에게 현재 접속한 플레이어 정보 전송
					Log.info(player.getName() + "(" + ws.remoteAddress() + ")가 접속했습니다.");
					Animals.gui.refreshPlayerList();
				} catch (Exception e) {
					Log.error(e);
				}
				break;
				
			case "chat":
				Chat chat = gson.fromJson(gson.toJson(packet.getData()), Chat.class);
				Log.info("<" + chat.getName() + "> " + chat.getMessage());
				sendAll(new AnimalsPacket("chat", chat));
				break;
				
			case "move":
				Player targetplayer = gson.fromJson(gson.toJson(packet.getData()), Player.class);
				if(isAlreadyName(targetplayer.getName())) {
					Player player = getPlayer(targetplayer.getName());
					player.setX(targetplayer.getX());
					player.setY(targetplayer.getY());
					sendAll(new AnimalsPacket("move", player));
				} else { // 비정상적인 접근자
					Log.warning(ws.remoteAddress() + "에서 비정상적인 움직임을 시도했습니다.");
					send(ws, new AnimalsPacket("kick", 2));
					ws.close();
					return;
				}
				break;
				
			case "ready":
				try {
					String readyer = (String) packet.getData();
					getPlayer(readyer).setReady(true);
					Log.info(readyer + "가 준비했습니다.");
					sendAll(new AnimalsPacket("ready", new Ready(readyer)));
					
					if(isAllReady(Animals.MIN_PLAYER)) { // 2명 이상 이고 모두 레디 눌렀을때
						if(Animals.isStarted)
							return;
						if(Animals.isIniting)
							return;
						Animals.isIniting = true;
						
						int temp;
						Random random = new Random();
						for(Player p : Animals.onlinePlayers) { // 모두에게 랜덤으로 동물 배정
							try {
								p.getAnimal();
								continue;
							} catch(Exception e) {
								temp = random.nextInt(8); // 7 랜덤생성
								if(temp == 0)
									p.setAnimal(AnimalType.치타);			
								else if(temp == 1)
									p.setAnimal(AnimalType.얼룩말);		
								else if(temp == 2)
									p.setAnimal(AnimalType.악어);		
								else if(temp == 3)
									p.setAnimal(AnimalType.하마);		
								else if(temp == 4)
									p.setAnimal(AnimalType.말);		
								else if(temp == 5)
									p.setAnimal(AnimalType.사슴);	
								else if(temp == 6)
									p.setAnimal(AnimalType.사자);			
								else if(temp == 7)
									p.setAnimal(AnimalType.유인원);		
							}							
								
							sendAll(new AnimalsPacket("changeProfile", p)); // 바뀐 프로필 적용
						}
						
						Thread tempThread = new Thread(() -> {
							for(int i = 3; i > 0; i--) {
								Log.info(i + "초 후 게임 시작...");
								sendAll(new AnimalsPacket("waitTostart", i)); // 게임 시작 전 카운트 다운
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									Log.error(e);
								}
							}
							Log.info("게임이 시작되었습니다.");
							Animals.isStarted = true; // 게임 시작 기록
							Animals.isIniting = false;
							sendAll(new AnimalsPacket("startgame", 1)); // 전원에게 게임 시작
						});
						tempThread.start();
						
					}
						
					
				} catch(Exception e) {
					Log.error(e);
				}
			}

		});

		ws.closeHandler(v -> { // 한 클라이언트의 접속 끊겼을때
			if (isOnline(ws)) {
				Player player = getPlayer(ws);
				Log.info(player.getName() + "(" + ws.remoteAddress() + ")가 나갔습니다.");
				Animals.onlinePlayers.remove(player);
				sendAll(new AnimalsPacket("leave", player));
				Animals.gui.refreshPlayerList();
				ws.close();
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
	
	public Player getPlayer(String name) {
		return Animals.onlinePlayers.stream().filter(obj -> obj.getName().equals(name)).findFirst().get();
	}
	
	public boolean isAlreadyName(String name) {
		return Animals.onlinePlayers.stream().anyMatch(obj -> obj.getName().equals(name));
	}
	
	public boolean isAllReady(int min_Player) {
		if(Animals.onlinePlayers.size() < min_Player)
			return false;
		return Animals.onlinePlayers.stream().allMatch(obj -> obj.isReady() == true);
	}


}

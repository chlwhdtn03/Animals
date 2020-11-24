package server;

import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import animals.Animals;
import data.AnimalType;
import data.Chat;
import data.Damage;
import data.Dead;
import data.Map;
import data.MapType;
import data.Player;
import data.Ready;
import data.Vector2D;
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
			case "attack": {
				Player player = gson.fromJson(gson.toJson(packet.getData()), Player.class);
				sendAll(new AnimalsPacket("attack", player));
				try {

					if(player.getDirection().equals("right")) { // 오른쪽을 바라보고 있을 때
						Vector2D attack_zone = new Vector2D(player.getX()+player.getAnimal().getWidth(), player.getY(),
								player.getX()+player.getAnimal().getWidth()+player.getAnimal().getHeight(), player.getY()+player.getAnimal().getHeight());
						// 무기의 크기는 캐릭터의 높이값과 동일
						
						for(Player target : Animals.onlinePlayers) {
							if(target.getAnimal() == null) continue; // 관전자는 검사할 필요 X
							if(target.getName().equals(player.getName())) continue; // 동일인은 검사할 필요 X
							
							if(Vector2D.isCoveredWithVector2D(attack_zone, target.getVector2D())) { // 피격 당하면
								onDamageEvent(player, target, 10);
							}
							
						}
					} else if(player.getDirection().equals("left")) { // 왼쪽을 바라보고 있을 때
						Vector2D attack_zone = new Vector2D(player.getX()-player.getAnimal().getHeight(), player.getY(),
								player.getX(), player.getY()+player.getAnimal().getHeight());
						
						for(Player target : Animals.onlinePlayers) {
							if(target.getAnimal() == null) continue; // 관전자는 검사할 필요 X
							if(target.getName().equals(player.getName())) continue; // 동일인은 검사할 필요 X
							
							if(Vector2D.isCoveredWithVector2D(attack_zone, target.getVector2D())) { // 피격 당하면
								onDamageEvent(player, target, 10);
							}
							
						}
					}
				} catch(Exception e) {
					Log.error(e);
				}
				break;
			}
				
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
					
					if(Animals.isStarted || Animals.isIniting)
						send(ws, new AnimalsPacket("changeMAP", new Map(Animals.map))); // 현재 맵 전송
					
					for (Player p : Animals.onlinePlayers) {
						send(ws, new AnimalsPacket("join", p)); // 지금 접속한 플레이어에게 모든 플레이어 정보 전송
					}
					Animals.onlinePlayers.add(player); // 지금 접속한 플레이어 저장

					sendAll(new AnimalsPacket("join", player)); // 모든 플레이어에게 현재 접속한 플레이어 정보 전송
					
					send(ws, new AnimalsPacket("started", Animals.isStarted));
					
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
					player.setDirection(targetplayer.getDirection());
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
					sendAll(new AnimalsPacket("ready", new Ready(readyer, true)));
					
					if(isAllReady(Animals.MIN_PLAYER)) { // 2명 이상 이고 모두 레디 눌렀을때
						if(Animals.isStarted)
							return;
						if(Animals.isIniting)
							return;
						Animals.isIniting = true;
						
						int temp;
						Random random = new Random();
						for(Player p : Animals.onlinePlayers) { // 모두에게 랜덤으로 동물 배정
							
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
								
								p.setHealth(p.getAnimal().getMaxhealth());
								sendAll(new AnimalsPacket("changeProfile", p)); // 바뀐 프로필 적용
						}
						
						switch(random.nextInt(2)) {
						case 0:
							Animals.map = MapType.Field; break;
						case 1:
							Animals.map = MapType.Desert; break;
						}
						sendAll(new AnimalsPacket("changeMAP", new Map(Animals.map))); // 현재 맵 전송
						
						Thread tempThread = new Thread(() -> {
							for(int i = Animals.startCount; i > 0; i--) {
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
				
				Collector<Player, ?, List<Player>> collector = Collectors.toList();
				int count = Animals.onlinePlayers.stream().filter(p->p.getAnimal()!=null).collect(collector).size();
				if(count< Animals.MIN_PLAYER) {
					resetGame(0);
				}					
			}
		});

	}

	private void onDamageEvent(Player player, Player target, int damage) {

		sendAll(new AnimalsPacket("damage", new Damage(player.getName(), target.getName(), damage)));
		target.setHealth(target.getHealth() - damage);
		Log.info(target.getName() + target.getHealth());
		if(target.getHealth() <= 0) {
			Log.info(target.getName() + " 사망! 킬러:" + player.getName());
			target.makeSpectator();
			sendAll(new AnimalsPacket("dead", new Dead(player, target)));
			
			Collector<Player, ?, List<Player>> collector = Collectors.toList();
			int count = Animals.onlinePlayers.stream().filter(p->p.getAnimal()!=null).collect(collector).size();
			if(count == 1) { // 남은 생존자가 1명이라면
				Player winner = Animals.onlinePlayers.stream().filter(p->p.getAnimal()!=null).findAny().get();
				sendAll(new AnimalsPacket("winner", winner));
				Thread tempThread = new Thread(() -> {
					try {
						Thread.sleep(Animals.startCount * 1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					resetGame(1);
				});
				tempThread.start();
				
				
			}
			
		}
		
	}

	/**
	 * @see 접속자 모두에게 데이터 전송
	 * @param msg 전송할 내용
	 */
	public static void sendAll(AnimalsPacket packet) {

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
	
	public void resetGame(int stopcode) { // 0 : 인원 부족 종료, 1 : 승리 종료
		for(Player p : Animals.onlinePlayers) {
			p.setAnimal(null);
			p.setReady(false);
			sendAll(new AnimalsPacket("stopgame", stopcode));
			sendAll(new AnimalsPacket("changeProfile", p));
			sendAll(new AnimalsPacket("ready", new Ready(p.getName(), false)));
		}
		
		Animals.isIniting = false;
		Animals.isStarted = false;
		
		switch(stopcode) {
		case 0:
			Log.info("플레이어가 부족하여 게임이 중단되었습니다.");
			break;
		case 1:
			Log.info("플레이어가 부족하여 게임이 중단되었습니다.");
			break;
		}
		
	}


}

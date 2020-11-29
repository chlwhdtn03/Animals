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
import data.MovePacket;
import data.Player;
import data.Ready;
import data.StringPacket;
import data.Vector2D;
import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.net.SocketAddress;
import packet.AnimalsPacket;
import util.Log;

public class ConnectionListener implements Handler<ServerWebSocket> {
	
	@Override
	public void handle(ServerWebSocket ws) {

		ws.frameHandler(frame -> { // 한 클라이언트가 메세지를 보낼 때

			Gson gson = new Gson();
			AnimalsPacket packet = gson.fromJson(frame.textData(), AnimalsPacket.class);

			switch (packet.getType()) { // server.js 참고
			case "attack": {
				Player player = gson.fromJson(gson.toJson(packet.getData()), Player.class);
				Player attacker = getPlayer(player.getName());
				if(attacker.isAttacking())
					return;
				attacker.setAttacking(true);
				
				Thread tempThread = new Thread(() -> { // 서버단에서 공격 쿨타임 관리
					try {
						Thread.sleep(995);
						attacker.setAttacking(false);
					} catch (InterruptedException e) {
						Log.error(e);
					}
				});
				tempThread.start();
				
				if(attacker.getX() != player.getX() || attacker.getY() != player.getY()) {
					send(attacker.getWs(), new AnimalsPacket("move", attacker));
				}
				
				sendAll(new AnimalsPacket("attack", attacker));
				try {
					
					if(attacker.getDirection().equals("right")) { // 오른쪽을 바라보고 있을 때
						Vector2D attack_zone = new Vector2D(attacker.getX()+attacker.getAnimal().getWidth(), attacker.getY(),
								attacker.getX()+attacker.getAnimal().getWidth()+attacker.getAnimal().getHeight(), attacker.getY()+attacker.getAnimal().getHeight());
						// 무기의 크기는 캐릭터의 높이값과 동일
						
						for(Player target : Animals.onlinePlayers) {
							if(target.getAnimal() == null) continue; // 관전자는 검사할 필요 X
							if(target.getName().equals(attacker.getName())) continue; // 동일인은 검사할 필요 X
							
							if(Vector2D.isCoveredWithVector2D(attack_zone, target.getVector2D())) { // 피격 당하면
								onDamageEvent(attacker, target, 5);
							}
							
						}
					} else if(player.getDirection().equals("left")) { // 왼쪽을 바라보고 있을 때
						Vector2D attack_zone = new Vector2D(attacker.getX()-attacker.getAnimal().getHeight(), attacker.getY(),
								attacker.getX(), attacker.getY()+attacker.getAnimal().getHeight());
						
						for(Player target : Animals.onlinePlayers) {
							if(target.getAnimal() == null) continue; // 관전자는 검사할 필요 X
							if(target.getName().equals(attacker.getName())) continue; // 동일인은 검사할 필요 X
							
							if(Vector2D.isCoveredWithVector2D(attack_zone, target.getVector2D())) { // 피격 당하면
								onDamageEvent(attacker, target, 5);
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
					
					if(player.getName().length() > 20 && Animals.isAllow_longName.isValue() == false) {
						Log.warning(ws.remoteAddress() + "에서 너무 긴 닉네임을 사용하여 연결 해제하였습니다.");
						send(ws, new AnimalsPacket("kick", 6));
						ws.close();
						return;
					}
					
					if(isAlreadyIP(ws.remoteAddress()) && Animals.isAllow_sameIP.isValue() == false) {
						Log.warning(ws.remoteAddress() + "님은 이미 동일 IP에서 접속중입니다.");
						send(ws, new AnimalsPacket("kick", 3));
						ws.close();
						return;
					}
					
					player.setWs(ws);
					send(ws, new AnimalsPacket("build", new StringPacket(Animals.version)));
					
					if(Animals.isStarted.isValue() || Animals.isIniting.isValue())
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
				
			case "move": {
				MovePacket movedata = gson.fromJson(gson.toJson(packet.getData()), MovePacket.class);
				if(isAlreadyIP(ws.remoteAddress())) {
					Player player = getPlayer(ws);
					if(movedata.getDx() > 2)
						movedata.setDx(2);
					if(movedata.getDy() > 2)
						movedata.setDy(2);
					
					player.setX(player.getX() + movedata.getDx());
					player.setY(player.getY() + movedata.getDy());
					player.setDirection(movedata.getDirection());
					sendAll(new AnimalsPacket("move", player));
						
				} else { // 비정상적인 접근자
					Log.warning(ws.remoteAddress() + "에서 비정상적인 움직임을 시도했습니다.");
					KickPlayer(getPlayer(ws), 2);
					ws.close();
				}
				break;
			}
				
			case "ready":
				try {
					String readyer = (String) packet.getData();
					getPlayer(readyer).setReady(true);
					Log.info(readyer + "가 준비했습니다.");
					sendAll(new AnimalsPacket("ready", new Ready(readyer, true)));
					
					checkMinPlayer();						
					
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
				Animals.gui.refreshPlayerList();
				ws.close();
				sendAll(new AnimalsPacket("leave", player));
				
				checkMinPlayer();
			}
		});

	}

	public static void checkMinPlayer() {
		if(Animals.isStarted.isValue()) { // 게임이 진행중일때
			Collector<Player, ?, List<Player>> collector = Collectors.toList();
			int count = Animals.onlinePlayers.stream().filter(p->p.getAnimal()!=null).collect(collector).size(); // 생존자 수 구함
			if(count< Animals.MIN_PLAYER) { // 생존자 수가 게임을 진행하는데 필요한 최소인원보다 적으면
				resetGame(0); // 리셋
			}					
		} else {
			if(isAllReady(Animals.MIN_PLAYER)) { // 2명 이상 이고 모두 레디 눌렀을때
				if(Animals.isStarted.isValue())
					return;
				
				int temp;
				Random random = new Random();
				
				switch(random.nextInt(2)) {
				case 0:
					Animals.map = MapType.Field; break;
				case 1:
					Animals.map = MapType.Desert; break;
				}
				sendAll(new AnimalsPacket("changeMAP", new Map(Animals.map))); // 현재 맵 전송
				
				
				for(Player p : Animals.onlinePlayers) { // 모두에게 랜덤으로 동물 배정
						if(p.getAnimal() != null)
							continue;
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
						p.setX(random.nextInt(Animals.map.getWidth()-500)+250);
						p.setY(random.nextInt(Animals.map.getHeight()-500)+250);
						sendAll(new AnimalsPacket("move", p)); // 랜덤 좌표 전송
						sendAll(new AnimalsPacket("changeProfile", p)); // 바뀐 프로필 적용
				}
				if(Animals.isIniting.isValue())
					return;

				Animals.isIniting.setValue(true);
				
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
					Animals.isStarted.setValue(true); // 게임 시작 기록
					Animals.isIniting.setValue(false);
					sendAll(new AnimalsPacket("startgame", 1)); // 전원에게 게임 시작
				});
				tempThread.start();
			}

		}
		
	}

	public static void KickPlayer(Player player, int kickcode) {
		send(player.getWs(), new AnimalsPacket("kick", kickcode));
		sendAll(new AnimalsPacket("leave", player));
		player.getWs().close();
		Animals.onlinePlayers.remove(player);
		Animals.gui.refreshPlayerList();
		
		checkMinPlayer();
	}

	private void onDamageEvent(Player attacker, Player target, int damage) {
		
		sendAll(new AnimalsPacket("damage", new Damage(attacker.getName(), target.getName(), damage)));
		target.setHealth(target.getHealth() - damage);
		
		if(target.getHealth() <= 0) {
			Log.info(target.getName() + "님이 사망하였습니다!");
			attacker.setHealth(attacker.getHealth() + 25);
			target.makeSpectator();

			Collector<Player, ?, List<Player>> collector = Collectors.toList();
			int count = Animals.onlinePlayers.stream().filter(p->p.getAnimal()!=null).collect(collector).size();
			
			sendAll(new AnimalsPacket("dead", new Dead(attacker, target, count)));
			
			if(count == 1) { // 남은 생존자가 1명이라면
				Player winner = Animals.onlinePlayers.stream().filter(p->p.getAnimal()!=null).findAny().get();
				sendAll(new AnimalsPacket("winner", winner));
				Thread tempThread = new Thread(() -> {
					try {
						Thread.sleep(Animals.startCount * 1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						Log.error(e);
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

	public static void send(ServerWebSocket ws, AnimalsPacket packet) {
		try {
			ws.writeFinalTextFrame(packet.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isOnline(ServerWebSocket ws) {
		return Animals.onlinePlayers.stream().anyMatch(obj -> obj.getWs().equals(ws));
	}

	public static Player getPlayer(ServerWebSocket ws) {
		return Animals.onlinePlayers.stream().filter(obj -> obj.getWs().equals(ws)).findFirst().get();
	}
	
	public static Player getPlayer(String name) {
		return Animals.onlinePlayers.stream().filter(obj -> obj.getName().equals(name)).findFirst().get();
	}
	
	public boolean isAlreadyName(String name) {
		return Animals.onlinePlayers.stream().anyMatch(obj -> obj.getName().equals(name));
	}
	
	public boolean isAlreadyIP(SocketAddress ip) {
		return Animals.onlinePlayers.stream().anyMatch(obj -> obj.getWs().remoteAddress().host().equals(ip.host()));
	}
	
	public static boolean isAllReady(int min_Player) {
		if(Animals.onlinePlayers.size() < min_Player)
			return false;
		return Animals.onlinePlayers.stream().allMatch(obj -> obj.isReady() == true);
	}
	
	public static void resetGame(int stopcode) { // 0 : 인원 부족 종료, 1 : 승리 종료
		Animals.isIniting.setValue(false);
		Animals.isStarted.setValue(false);
		
		for(Player p : Animals.onlinePlayers) {
			p.setAnimal(null);
			p.setReady(false);
			p.setX(0);
			p.setY(0);
			p.setDirection("right");
			sendAll(new AnimalsPacket("stopgame", stopcode));
			sendAll(new AnimalsPacket("changeProfile", p));
			sendAll(new AnimalsPacket("move", p));
			sendAll(new AnimalsPacket("ready", new Ready(p.getName(), false)));
		}
		
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

package server;

import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;

public class ConnectionListener implements Handler<ServerWebSocket> {

	@Override
	public void handle(ServerWebSocket ws) {
		
		ws.frameHandler(frame -> { // 한 클라이언트가 메세지를 보낼 때
			
		});
		
		ws.closeHandler(v -> { // 한 클라이언트의 접속 끊겼을때
			
		});
		
	}

}

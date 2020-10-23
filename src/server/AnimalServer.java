package server;

import java.io.InputStream;

import animals.Animals;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import util.Log;

public class AnimalServer {

	private HttpServer server;
	
	public AnimalServer() {
		Log.info("서버 초기화...");
		server = Vertx.vertx().createHttpServer(new HttpServerOptions().setPort(Animals.port)).requestHandler(req -> {
			
			try (InputStream in = getClass().getResourceAsStream("/web" + (req.path().equals("/") ? "/index.html" : req.path()))) {
				byte[] data = new byte[1024];
				int size;
				Buffer buffer = Buffer.factory.buffer();
				while ((size = in.read(data)) != -1) {
					buffer.appendBytes(data, 0, size);
				}
				req.response().end(buffer);
			} catch (Exception e) {
				req.response().setStatusCode(404).end();
			}
			
		}).webSocketHandler(new ConnectionListener())
				.listen(Animals.port, result -> {
					if(result.succeeded()) {
						Log.info("정상적으로 개방되었습니다.");
					} else {
						Log.error("이미 사용중인 포트입니다.");
					}
				});
		
	}
	
}

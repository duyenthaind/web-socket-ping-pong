package org.example.websocket.pingclient;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author duyenthaind
 */
public class PingClient {

    private static final Logger LOGGER = Logger.getLogger("PingClient");

    public static void main(String[] args) throws Exception {
        String uri = "ws://local.stringee.com:8888/websocket";
        WebSocketPingClient client = new WebSocketPingClient(uri);
        client.open();
        Thread thread = new Thread() {

            @Override
            public void run() {
                while (true) {
                    try {
                        client.ping();
                    } catch (Exception ex) {
                        LOGGER.log(Level.SEVERE, "Ping error ", ex);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        LOGGER.log(Level.SEVERE, "Interrupted signal sent ", e);
                    }

                }
            }
        };
        thread.start();
        TimeUnit.SECONDS.sleep(20);
        thread.interrupt();
//        thread.stop();
        LOGGER.info("Call stopped thread ");
    }
}

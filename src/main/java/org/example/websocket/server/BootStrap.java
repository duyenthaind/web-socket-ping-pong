package org.example.websocket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author duyenthaind
 */
public class BootStrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(BootStrap.class);

    public static void main(String[] args) throws Exception {

        LOGGER.info("OK!");

        WebSocketServer wsServer = new WebSocketServer();
        wsServer.start(8888);
    }
}

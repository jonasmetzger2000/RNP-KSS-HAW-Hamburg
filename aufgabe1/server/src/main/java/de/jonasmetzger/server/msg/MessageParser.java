package de.jonasmetzger.server.msg;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageParser {

    final static Logger logger = LogManager.getLogger(MessageParser.class);

    public Response parse(String msg, String client) {
        if (msg.contains("\r")) return Response.err("\\r_NOT_ALLOWED");
        if (msg.startsWith("LOWERCASE")) {
            final String params = msg.substring(9).strip();
            if (params.isEmpty()) {
                logger.info("LOWERCASE Command Received with missing Params from client {}", client);
                return Response.err("PARAMS_MISS");
            } else {
                logger.info("LOWERCASE Command Received from client {}", client);
                return Response.ok(params.toLowerCase());
            }
        } else if (msg.startsWith("UPPERCASE")) {
            final String params = msg.substring(9).strip();
            if (params.isEmpty()) {
                logger.info("UPPERCASE Command Received with missing Params from client {}", client);
                return Response.err("PARAMS_MISS");
            } else {
                logger.info("UPPERCASE Command Received from client {}", client);
                return Response.ok(params.toUpperCase());
            }
        } else if (msg.startsWith("REVERSE")) {
            final String params = msg.substring(7).strip();
            if (params.isEmpty()) {
                logger.info("REVERSE Command Received with missing Params from client {}", client);
                return Response.err("PARAMS_MISS");
            } else {
                logger.info("REVERSE Command Received from client {}", client);
                return Response.ok(new StringBuilder(params).reverse().toString());
            }
        } else if (msg.startsWith("SHUTDOWN")) {
            final String params = msg.substring(8).strip();
            if (!params.equals("HAW")) {
                logger.info("SHUTDOWN Command Received with incorrect Password from client {}", client);
                return Response.err("INVALID_PWD");
            } else {
                logger.info("SHUTDOWN Command Received from client {}", client);
                return Response.terminate();
            }
        } else if (msg.startsWith("BYE")) {
            logger.info("BYE Command Received from client {}", client);
            return Response.sessionEnd();
        }
        logger.info("Invalid Command Received from client {}", client);
        return Response.err("INVALID_CMD");
    }
}

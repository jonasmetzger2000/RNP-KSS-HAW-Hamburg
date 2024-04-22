package msg;

public class MessageParser {

    public Response parse(String msg) {
        if (msg.startsWith("LOWERCASE")) {
            final String params = msg.substring(9).strip();
            if (params.isEmpty()) return Response.err("PARAMS_MISS");
            else return Response.ok(params.toLowerCase());
        } else if (msg.startsWith("UPPERCASE")) {
            final String params = msg.substring(9).strip();
            if (params.isEmpty()) return Response.err("PARAMS_MISS");
            else return Response.ok(params.toUpperCase());
        } else if (msg.startsWith("REVERSE")) {
            final String params = msg.substring(7).strip();
            if (params.isEmpty()) return Response.err("PARAMS_MISS");
            else return Response.ok(new StringBuilder(params).reverse().toString());
        } else if (msg.startsWith("SHUTDOWN")) {
            final String params = msg.substring(8).strip();
            if (!params.equals("HAW")) return Response.err("INVALID_PWD");
            else return Response.terminate();
        } else if (msg.startsWith("BYE")) {
            return Response.sessionEnd();
        }
        return Response.err("INVALID_CMD");
    }
}

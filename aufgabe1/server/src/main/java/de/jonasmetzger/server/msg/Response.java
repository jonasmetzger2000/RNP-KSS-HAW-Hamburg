package de.jonasmetzger.server.msg;

import lombok.Getter;

@Getter
public class Response {

    protected final boolean termination;
    protected final boolean sessionEnd;
    protected final boolean error;
    protected final String msg;

    private Response(boolean termination, boolean sessionEnd, boolean error, String msg) {
        this.termination = termination;
        this.sessionEnd = sessionEnd;
        this.error = error;
        this.msg = msg;
    }

    public static Response ok(String msg) {
        return new Response(false, false, false, msg);
    }

    public static Response err(String msg) {
        return new Response(false, false, true, msg);
    }

    public static Response sessionEnd() {
        return new Response(false, true, false, "BYE");
    }

    public static Response terminate() {
        return new Response(true, true, false, "BYE");
    }
    public static Response terminated() { return new Response(true, true, true, "SERVER_CLOSING"); }
    public static Response noConnectionsAvailable() { return new Response(false, true, true, "NO_CONN_AVAIL"); }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        if (error) stringBuilder.append("ERROR ");
        else stringBuilder.append("OK ");
        stringBuilder.append(msg);
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}

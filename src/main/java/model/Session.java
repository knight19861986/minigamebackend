package main.java.model;

import main.java.Utils;

import java.util.UUID;

public class Session {
    private final String sessionId;
    private final User user;
    private long expiryTime;
    private final StringBuilder sb;

    public Session(User user) {
        UUID uuid = UUID.randomUUID();
        this.sessionId = uuid.toString();
        this.user = user;
        this.expiryTime = System.currentTimeMillis() + 1000 * 60 * 10;
        this.sb = new StringBuilder();
    }

    private void visited() {
        this.expiryTime = System.currentTimeMillis() + 1000 * 60 * 10;

    }

    public int getUserId() {
        this.visited();
        return this.user.getUserId();
    }

    public String getSessionId() {

        return sessionId;
    }

    public boolean sessionIsValid() {
        return System.currentTimeMillis() < this.expiryTime;
    }

    @Override
    public String toString() {
        sb.append("SessionId: " + sessionId + "\n");
        sb.append("UserId: " + user.getUserId() + "\n");
        sb.append("ExpiryTime: " + Utils.timestampToDatetime(expiryTime) + "\n" + "\n");
        return sb.toString();
    }

}

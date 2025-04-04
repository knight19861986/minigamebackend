package main.java;

import main.java.model.HighScoreBoard;
import main.java.model.Session;
import main.java.model.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataTables {
    private final Map<String, Session> sessionTable;
    private final Map<Integer, User> userTable;
    private final Map<Integer, HighScoreBoard> boardTable;
    private final StringBuilder sb;

    public DataTables() {
        this.sessionTable = new ConcurrentHashMap<String, Session>();
        this.userTable = new ConcurrentHashMap<Integer, User>();
        this.boardTable = new ConcurrentHashMap<Integer, HighScoreBoard>();
        this.sb = new StringBuilder();
    }

    private boolean userIsExisted(int userId) {
        return this.userTable.containsKey(Integer.valueOf(userId));
    }

    public boolean createUser(int userId) {
        boolean res = false;
        if (!this.userIsExisted(userId)) {
            this.userTable.put(userId, new User(userId));
            res = true;
        }
        return res;
    }

    public boolean updateUserScore(int userId, int levelId, int score) {
        boolean res = false;
        if (this.userIsExisted(userId)) {
            this.userTable.get(userId).setScore(levelId, score);
            res = true;
        }
        return res;

    }

    private boolean highScoreBoardIsExisted(int levelId) {
        return this.boardTable.containsKey(Integer.valueOf(levelId));
    }

    public boolean createHighScoreBoard(int levelId) {
        boolean res = false;
        if (!this.highScoreBoardIsExisted(levelId)) {
            this.boardTable.put(levelId, new HighScoreBoard(levelId));
            res = true;
        }
        return res;
    }

    public boolean updateHighScoreBoard(int userId, int levelId) {
        boolean res = false;
        if (this.userIsExisted(userId)) {
            if (!this.highScoreBoardIsExisted(levelId))
                this.createHighScoreBoard(levelId);
            this.boardTable.get(levelId).updateBoard(this.userTable.get(userId));
            res = true;
        }
        return res;
    }

    public String getHighScoreBoard(int levelId) {
        if (this.highScoreBoardIsExisted(levelId)) {
            return this.boardTable.get(levelId).getCSV();
        }
        return "";
    }

    private boolean sessionIsExisted(String sessionId) {
        return this.sessionTable.containsKey(sessionId);
    }

    public boolean sessionIsValid(String sessionId) {
        boolean res = false;
        if (sessionIsExisted(sessionId))
            if (this.sessionTable.get(sessionId).sessionIsValid())
                res = true;
        return res;
    }

    public String createSession(int userId) {
        if (!this.userIsExisted(userId)) {
            this.createUser(userId);
        }
        Session session = new Session(this.userTable.get(userId));
        this.sessionTable.put(session.getSessionId(), session);
        return session.getSessionId();
    }

    public int getUserIdBySession(String sessionId) {
        int res = -1;
        if (this.sessionIsValid(sessionId)) {
            res = this.sessionTable.get(sessionId).getUserId();
        }
        return res;
    }

    public String outputUserTable() {
        sb.setLength(0);
        for (Map.Entry<Integer, User> entry : this.userTable.entrySet()) {
            sb.append(entry.getValue().toString()).append('\n');

        }
        return sb.toString().trim();
    }

    public String outputSessionTable() {
        sb.setLength(0);
        for (Map.Entry<String, Session> entry : this.sessionTable.entrySet()) {
            sb.append(entry.getValue().toString()).append('\n');

        }
        return sb.toString().trim();
    }

    public String outputBoardTable() {
        sb.setLength(0);
        for (Map.Entry<Integer, HighScoreBoard> entry : this.boardTable.entrySet()) {
            sb.append(entry.getValue().toString()).append('\n');

        }
        return sb.toString().trim();
    }
}

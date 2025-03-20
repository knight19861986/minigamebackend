package main.java;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataTables {
    private Map<String, Session> sessionTable;
    private Map<Integer, User>userTable;
    private Map<Integer, HighScoreBoard>boardTable;

    public DataTables() {
        this.sessionTable = new ConcurrentHashMap<String, Session>();
        this.userTable = new ConcurrentHashMap<Integer, User>();
        this.boardTable = new ConcurrentHashMap<Integer, HighScoreBoard>();
    }

    public boolean userIsExisted(int userId) {

        return this.userTable.containsKey(Integer.valueOf(userId));
    }

    public boolean createUser(int userId){
        boolean res =false;
        if(!this.userIsExisted(userId)){
            this.userTable.put(userId, new User(userId));
            res = true;
        }
        return res;
    }

    public boolean updateUserScore(int userId, int levelId, int score){
        boolean res =false;
        if(this.userIsExisted(userId)){
            this.userTable.get(userId).setScore(levelId, score);
            res = true;
        }
        return res;

    }

    public boolean removeUser(int userId){
        boolean res =false;
        if(this.userIsExisted(userId)){
            this.userTable.remove(userId);
            res = true;
        }
        return res;
    }

    public boolean highScoreBoardIsExisted(int levelId) {

        return this.boardTable.containsKey(Integer.valueOf(levelId));
    }


    public boolean createHighScoreBoard(int levelId){
        boolean res =false;
        if(!this.highScoreBoardIsExisted(levelId)){
            this.boardTable.put(levelId, new HighScoreBoard(levelId));
            res = true;
        }
        return res;

    }

    public boolean updateHighScoreBoard(int userId, int levelId){
        boolean res =false;
        if(this.userIsExisted(userId)){
            if(!this.highScoreBoardIsExisted(levelId))
                this.createHighScoreBoard(levelId);
            this.boardTable.get(levelId).updateBoard(this.userTable.get(userId));
            res = true;
        }
        return res;
    }

    public String getHighScoreBoard(int levelId){
        if (this.highScoreBoardIsExisted(levelId)) {
            return this.boardTable.get(levelId).getCSV();
        }
        return null;
    }



    public boolean sessionIsExisted(String sessionId) {

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

    public int getUserIdBySession(String sessionId){
        if (this.sessionIsValid(sessionId)) {
            return this.sessionTable.get(sessionId).getUserId();
        }
        return -1;
    }

    public boolean removeSession(String sessionId) {
        boolean res = false;
        if (this.sessionIsExisted(sessionId)) {
            this.sessionTable.remove(sessionId);
            res = true;
        }
        return res;

    }

    public String outputUserTable(){
        StringBuilder res = new StringBuilder();
        for(Map.Entry<Integer, User> entry:this.userTable.entrySet()){
            res.append(entry.getValue().toString());

        }
        return res.toString();
    }

    public String outputSessionTable(){
        StringBuilder res = new StringBuilder();
        for(Map.Entry<String, Session> entry:this.sessionTable.entrySet()){
            res.append(entry.getValue().toString());

        }
        return res.toString();
    }

    public String outputBoardTable(){
        StringBuilder res = new StringBuilder();
        for(Map.Entry<Integer, HighScoreBoard> entry:this.boardTable.entrySet()){
            res.append(entry.getValue().toString());

        }
        return res.toString();
    }
}

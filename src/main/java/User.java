package main.java;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class User {
    private final int userId;
    private Map<Integer,Integer> scores;
    private final long createTime;
    private long updateTime;

    public User(int userId) {
        this.userId = userId;
        this.scores = new ConcurrentHashMap<Integer,Integer>();
        this.createTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
    }

    public int getUserId() {

        return userId;
    }

    public int getScore(int levelId) {
        if (this.scores.containsKey(levelId)) {
            return scores.get(levelId);
        } else return -1;
    }

    public void setScore(int levelId, int score) {
        if ((!this.scores.containsKey(levelId))||score > this.scores.get(levelId))
            this.scores.put(levelId, score);
        this.timeUpdated();
    }

    private void timeUpdated(){

        this.updateTime = System.currentTimeMillis();
    }

    @Override
    public String toString(){
        StringBuilder res = new StringBuilder();
        res.append("UserId: " + userId + "\n");
        res.append("CreateTime: " + Utils.timestampToDatetime(createTime) + "\n");
        res.append("UpdateTime: " + Utils.timestampToDatetime(updateTime) + "\n");
        for(Map.Entry<Integer, Integer> entry:this.scores.entrySet()){
            res.append("LevelId: " + entry.getKey() + " ->");
            res.append("Score: " + entry.getValue() + "\n");
        }
        res.append("\n");

        return res.toString();
    }

}

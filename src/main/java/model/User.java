package main.java.model;


import main.java.Utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class User {
    private final int userId;
    private Map<Integer, Integer> scores;
    private final long createTime;
    private long updateTime;
    private final StringBuilder sb;

    public User(int userId) {
        this.userId = userId;
        this.scores = new ConcurrentHashMap<Integer, Integer>();
        this.createTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
        this.sb = new StringBuilder();
    }

    public int getUserId() {

        return userId;
    }

    public int getScore(int levelId) {
        return scores.getOrDefault(levelId, -1);
    }

    public void setScore(int levelId, int score) {
        if ((!this.scores.containsKey(levelId)) || score > this.scores.get(levelId))
            this.scores.put(levelId, score);
        this.timeUpdated();
    }

    private void timeUpdated() {

        this.updateTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        sb.setLength(0);
        sb.append("UserId: " + userId + "\n");
        sb.append("CreateTime: " + Utils.timestampToDatetime(createTime) + "\n");
        sb.append("UpdateTime: " + Utils.timestampToDatetime(updateTime) + "\n");
        for (Map.Entry<Integer, Integer> entry : this.scores.entrySet()) {
            sb.append("LevelId: " + entry.getKey() + " ; ");
            sb.append("Score: " + entry.getValue() + "\n");
        }
        sb.append("\n");

        return sb.toString();
    }
}

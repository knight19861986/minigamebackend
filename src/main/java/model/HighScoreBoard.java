package main.java.model;

import main.java.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoreBoard {

    private int levelId;
    private final int boardMaxSize = 15;
    private List<User> topScoreUsers;

    public HighScoreBoard(int levelId) {
        this.levelId = levelId;
        this.topScoreUsers = Collections.synchronizedList(new ArrayList<User>());
    }

    private boolean userIsHigherThan(User user1, User user2) {
        int score1 = user1.getScore(this.levelId);
        int score2 = user2.getScore(this.levelId);
        //First compare the scores, then compare the user IDs
        if (score1 > score2 || (score1 == score2 && user1.getUserId() > user2.getUserId()))
            return true;
        else
            return false;
    }

    /*******************************************************************************************************************
     Core method of sorting users in the list:
     * Sorting when inserting/updating;
     * First check if the user is in the list:
     ** If yes, find the rank of the user, then compare with others who are before him from lower rank to higher
     ** If no, add the user at the end of the list, then compare with others who are before him from lower rank to higher
     * If the users is higher than the compared user, then swap them
     * The complexity should be O(n) for twice:
     ** First to find the rank of the user: O(n), n<=15
     ** Second to compare and swap: O(n), n<=15
     * If the size of list is larger than 15, remove the last user
     *******************************************************************************************************************/
    public void updateBoard(User user) {
        if (user != null) {
            if (topScoreUsers.size() > 0) {
                int startPoint = topScoreUsers.size() - 1;
                int userRank = userRankInBoard(user);
                if (userRank == 0)
                    return;
                if (userRank > 0)
                    startPoint = userRank - 1;

                for (int i = startPoint; i >= 0; i--) {
                    User tempUser = topScoreUsers.get(i);
                    if (userIsHigherThan(user, tempUser)) {
                        if (i >= topScoreUsers.size() - 1)//Means the temp user is at the end of the list
                            topScoreUsers.add(tempUser);
                        else
                            topScoreUsers.set(i + 1, tempUser);
                        topScoreUsers.set(i, user);
                    } else {
                        if (i >= topScoreUsers.size() - 1)//Means the temp user is at the end of the list
                            topScoreUsers.add(user);
                        break;
                    }
                }
            } else
                topScoreUsers.add(user);

            while (topScoreUsers.size() > boardMaxSize) {
                topScoreUsers.remove(topScoreUsers.size() - 1);
            }
        }
    }

    private int userRankInBoard(User user) {
        int res = -1;
        for (int i = 0; i < topScoreUsers.size(); i++) {
            if (topScoreUsers.get(i).getUserId() == user.getUserId()) {
                res = i;
                break;
            }
        }
        return res;
    }

    public String getCSV() {
        StringBuilder res = new StringBuilder();
        for (User user : this.topScoreUsers) {
            res.append(user.getUserId());
            res.append("=");
            res.append(user.getScore(this.levelId));
            res.append(",");
        }
        res.deleteCharAt(res.length() - 1);
        return res.toString();
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("LevelId: " + levelId + "\n");
        for (User user : this.topScoreUsers) {
            res.append("UserId: " + user.getUserId() + " ->");
            res.append("Score: " + user.getScore(this.levelId) + "\n");
        }
        res.append("\n");

        return res.toString();
    }
}


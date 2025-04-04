package main.java.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HighScoreBoard {

    private int levelId;
    private final int boardMaxSize = 15;
    private List<User> topScoreUsers;
    private Map<Integer, Integer> topScoreUserRanks;
    private final StringBuilder sb;

    public HighScoreBoard(int levelId) {
        this.levelId = levelId;
        this.topScoreUsers = Collections.synchronizedList(new ArrayList<User>());
        this.topScoreUserRanks = Collections.synchronizedMap(new HashMap<Integer, Integer>());
        this.sb = new StringBuilder();
    }

    public List<User> getTopScoreUsers() {
        return topScoreUsers;
    }

    /*******************************************************************************************************************
     Core method of sorting users in the list of board of top scores :
     * Sorting when inserting/updating;
     * First check if the user is in the list:
     ** If yes, find the rank of the user from the map of topScoreUserRanks
     *** If the rank is 0, which means the user is already on top, then do nothing
     *** If the rank is higher than 0, remove the user form the list
     * Apply binary search to find the proper position to insert the user into the list
     * The complexity should be O(logˆn)
     * If the size of list is larger than 15, remove the last user
     * Update the map of topScoreUserRanks
     *******************************************************************************************************************/
    public void updateBoard(User user) {
        //If the list is empty, add the user directly
        if (topScoreUsers.isEmpty()) {
            topScoreUsers.add(user);
            return;
        }
        //Check if the user is in the list, return -1 means not in the list
        int userRank = userRankInBoard(user);
        //If the user is on top, do nothing
        if (userRank == 0)
            return;
        //If the user is in the list, remove it
        if (userRank > 0)
            topScoreUsers.remove(userRank);
        //Apply binary search to find the proper position to insert the user into the list
        int worstRank = topScoreUsers.size() - 1;
        int bestRank = 0;
        int midRank = (worstRank + bestRank) / 2;
        while (worstRank >= bestRank) {
            midRank = (worstRank + bestRank) / 2;
            if (userIsPriorTo(user, topScoreUsers.get(midRank))) {
                worstRank = midRank - 1;
            } else {
                bestRank = midRank + 1;
            }
        }

        if (userIsPriorTo(user, topScoreUsers.get(midRank)))
            topScoreUsers.add(midRank, user);
        else
            topScoreUsers.add(midRank + 1, user);
        //If the size of list is larger than 15, remove the last user
        while (topScoreUsers.size() > boardMaxSize) {
            topScoreUsers.remove(topScoreUsers.size() - 1);
        }

        UpdateRankMap();
    }

    private void UpdateRankMap() {
        topScoreUserRanks = IntStream.range(0, topScoreUsers.size())
                .boxed()
                .collect(Collectors.toMap(i -> topScoreUsers.get(i).getUserId(), i -> i));
    }

    private int userRankInBoard(User user) {
        return topScoreUserRanks.getOrDefault(user.getUserId(), -1);
    }

    private boolean userIsPriorTo(User user1, User user2) {
        int score1 = user1.getScore(this.levelId);
        int score2 = user2.getScore(this.levelId);
        //First compare the scores, then compare the user IDs
        if (score1 > score2 || (score1 == score2 && user1.getUserId() < user2.getUserId())) return true;
        else return false;
    }

    public String getCSV() {
        sb.setLength(0);
        for (User user : this.topScoreUsers) {
            sb.append(user.getUserId());
            sb.append("=");
            sb.append(user.getScore(this.levelId));
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public String toString() {
        sb.setLength(0);
        sb.append("LevelId: " + levelId + "\n");
        for (User user : this.topScoreUsers) {
            sb.append("UserId: " + user.getUserId() + "; ");
            sb.append("Score: " + user.getScore(this.levelId) + "\n");
        }

        return sb.toString();
    }
}


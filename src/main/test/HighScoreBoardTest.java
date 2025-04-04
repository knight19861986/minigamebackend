package main.test;

import main.java.model.HighScoreBoard;
import main.java.model.User;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;


class HighScoreBoardTest {
    private List<User> users;
    private final int levelId = 1;
    private final int loopAmount = 3;
    private final int userAmount = 30;
    private final int scoreRange = 1000;//<1000
    private HighScoreBoard HSB;
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private FileHandler fh;
    private File logFile;

    @BeforeEach
    void setUp() throws IOException {
        users = new ArrayList<User>();
        for (int i = 0; i < userAmount; i++) {
            users.add(new User(i));
        }
        HSB = new HighScoreBoard(levelId);
        String logFileName = "./" +
                "Result_of_" +
                this.getClass().getName() +
                "_at_" + System.currentTimeMillis() + ".log";
        fh = new FileHandler(logFileName);
        logFile = new File(logFileName);
        LOGGER.addHandler(fh);
        LOGGER.setUseParentHandlers(false);
    }

    @AfterEach
    void tearDown() {
        System.out.println("Please read results in " + logFile.getAbsoluteFile().getPath());
    }

    @Test
    void updateBoard() {
        for (int i = 0; i < loopAmount; i++) {
            for (User user : users) {
                int score = (int) (Math.random() * scoreRange / (loopAmount + 1) * 2 + scoreRange / (loopAmount + 1) * i);
                user.setScore(levelId, score);
                HSB.updateBoard(user);
                LOGGER.info("User " + user.getUserId() + " scores " + score);
                LOGGER.info(HSB.toString());
            }
        }
        Assertions.assertEquals(15, HSB.getTopScoreUsers().size());
        Assertions.assertTrue(sortedCorrectly());
        Assertions.assertFalse(hasDuplicatedUsers());
    }

    private boolean sortedCorrectly() {
        for (int i = 0; i < HSB.getTopScoreUsers().size() - 1; i++) {
            if (HSB.getTopScoreUsers().get(i).getScore(levelId) < HSB.getTopScoreUsers().get(i+1).getScore(levelId))
                return false;
        }
        return true;
    }

    private boolean hasDuplicatedUsers() {
        for (int i = 0; i < HSB.getTopScoreUsers().size(); i++) {
            for (int j = i + 1; j < HSB.getTopScoreUsers().size(); j++) {
                if (HSB.getTopScoreUsers().get(i).getUserId() == HSB.getTopScoreUsers().get(j).getUserId()) {
                    LOGGER.info("Duplicated Users found: user "
                            + HSB.getTopScoreUsers().get(i).getUserId()
                            + " at rank " + i + " and rank " + j);
                    return true;
                }
            }
        }
        return false;
    }
}
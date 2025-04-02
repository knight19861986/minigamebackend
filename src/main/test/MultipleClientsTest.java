package main.test;

import com.sun.net.httpserver.HttpServer;
import main.java.DataTables;
import main.java.handlers.MainHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;
import static main.java.Utils.CHARSET;
import static main.java.Utils.stringFromStream;
import static org.junit.jupiter.api.Assertions.*;

class MultipleClientsTest {
    private final static String address = "localhost";
    private final static int port = 8083;
    private final static int capacity = 1000;
    private final static int delayInSeconds = 3;
    private final int levelAmount = 5;
    private final int postAmountPerUser = 10;
    private final int userAmount = 1000;
    private final int scoreRange = 1000;//<1000
    DataTables db;
    HttpServer httpServer;
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private FileHandler fh;
    private File logFile;

    @BeforeEach
    void setUp() throws IOException {
        db = new DataTables();
        String logFileName = "./" +
                "Result_of_" +
                this.getClass().getName() +
                "_at_" + System.currentTimeMillis() + ".log";
        fh = new FileHandler(logFileName);
        logFile = new File(logFileName);
        LOGGER.addHandler(fh);
        LOGGER.setUseParentHandlers(false);
        httpServer = HttpServer.create(new InetSocketAddress(address, port), 0);
        httpServer.createContext("/", new MainHandler(db));
        httpServer.setExecutor(Executors.newFixedThreadPool(capacity));
        httpServer.start();
    }

    @AfterEach
    void tearDown() throws IOException{
        httpServer.stop(delayInSeconds);
        LOGGER.info(db.outputSessionTable());
        LOGGER.info(db.outputUserTable());
        LOGGER.info(db.outputBoardTable());

        System.out.println("Please read results in " + logFile.getAbsoluteFile().getPath());
    }

    @Test
    void testSingleThread() throws IOException{
        for(int j=0; j<userAmount; j++){
            int userId = j;
            String sessionKey = getSessionKey(userId);
            for(int i = 0; i< postAmountPerUser; i++){
                int levelId = (int)(Math.random() *(levelAmount));
                int score = (int) (Math.random() * scoreRange/(postAmountPerUser +1)*2 + scoreRange/(postAmountPerUser +1)*i);
                assertEquals(200, postScore(levelId, sessionKey, score));
            }
        }
        for(int i=0; i<levelAmount; i++){
            LOGGER.info(getHighScoreList(i));
        }

    }

    @Test
    void testMultipleThreads() throws IOException, InterruptedException{
        for(int i=0; i<userAmount; i++){
            new singleClientThread(i).run();
        }
        sleep(delayInSeconds*1000);
        for(int i=0; i<levelAmount; i++){
            LOGGER.info(getHighScoreList(i));
        }

    }

    private class singleClientThread implements Runnable{
        private int userId;

        public singleClientThread(int userId) {
            super();
            this.userId = userId;
        }

        @Override
        public void run() {
            try {
                String sessionKey = getSessionKey(userId);
                for(int i = 0; i< postAmountPerUser; i++){
                    int levelId = (int)(Math.random() *(levelAmount));
                    int score = (int) (Math.random() * scoreRange/(postAmountPerUser +1)*2 + scoreRange/(postAmountPerUser +1)*i);
                    postScore(levelId, sessionKey, score);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private String getSessionKey(int userId) throws IOException{
        String fullPath = "http://" + address + ":" + port + "/" + userId + "/login";
        URL url = new URL(fullPath);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        String responseBody = stringFromStream(connection.getInputStream());
        return responseBody;
    }

    private int postScore(int levelId, String sessionKey, int score) throws IOException{
        String fullPath = "http://" + address + ":" + port + "/" + levelId + "/score?sessionkey=" + sessionKey;
        URL url = new URL(fullPath);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        OutputStream osOfConnection = connection.getOutputStream();
        if(osOfConnection != null)
            osOfConnection.write(String.valueOf(score).getBytes(CHARSET));
        int res=connection.getResponseCode();
        return res;

    }

    private String getHighScoreList(int levelId) throws IOException{
        String fullPath = "http://" + address + ":" + port + "/" + levelId + "/highscorelist";
        URL url = new URL(fullPath);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        String responseBody = stringFromStream(connection.getInputStream());
        return responseBody;
    }
}
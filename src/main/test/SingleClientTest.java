package main.test;

import com.sun.net.httpserver.HttpServer;
import main.java.handlers.AdminHandler;
import main.java.DataTables;
import main.java.handlers.MainHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.logging.Logger;

import static main.java.Utils.CHARSET;
import static main.java.Utils.stringFromStream;
import static org.junit.jupiter.api.Assertions.*;

class SingleClientTest {
    private final static String address = "localhost";
    private final static int port = 8082;
    private final static int delayInSeconds = 0;
    DataTables db;
    HttpServer httpServer;
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
//    private FileHandler fh;
//    private File logFile;

    @BeforeEach
    void setUp() throws IOException {
        db = new DataTables();
        String logFileName = "./" +
                "Result_of_" +
                this.getClass().getName() +
                "_at_" + System.currentTimeMillis() + ".log";
//        fh = new FileHandler(logFileName);
//        logFile = new File(logFileName);
//        LOGGER.addHandler(fh);
//        LOGGER.setUseParentHandlers(false);
        httpServer = HttpServer.create(new InetSocketAddress(address, port), 0);
        httpServer.createContext("/", new MainHandler(db));
        httpServer.createContext("/admin",new AdminHandler(db));
        httpServer.setExecutor(null);
        httpServer.start();
    }

    @AfterEach
    void tearDown() {
        httpServer.stop(delayInSeconds);
    }

    @Test
    void uRITest() throws Exception{
        assertGet("/admin", 200, "Admin!");
        assertGet("", 200, "Welcome!");
        assertGet("/", 200, "Welcome!");
        assertGet("/login", 200, "Welcome!");
        assertGet("/a/login", 200, "Welcome!");
        String sessionKey = assertGet("/1/login", 200);
        assertGet("/a/score", 200, "Welcome!");
        assertGet("/1/score", 200,"Welcome!");
        assertGet("/1/score?", 200,"Welcome!");
        assertGet("/1/score?a=b", 200,"Welcome!");
        assertGet("/1/score?a=b&c=d", 200,"Welcome!");
        assertGet("/1/score?sessionkey=a", 403);
        assertGet("/1/score?sessionkey=a&a=b", 403);
        assertGet("/a/highscorelist", 200, "Welcome!");
        assertGet("/1/highscorelist", 200, "");
        assertPost("/1/score?sessionkey=a","" ,403);
        assertPost("/1/score?sessionkey=" + sessionKey,"" ,403);
        assertPost("/1/score?sessionkey=" + sessionKey,"a" ,403);
        assertPost("/1/score?sessionkey=" + sessionKey,"01" ,403);
        assertPost("/1/score?sessionkey=" + sessionKey,"100" ,200);
        assertGet("/1/highscorelist", 200, "1=100");
        assertPost("/1/score?sessionkey=" + sessionKey,"50" ,200);
        assertGet("/1/highscorelist", 200, "1=100");
        assertPost("/1/score?sessionkey=" + sessionKey,"200" ,200);
        assertGet("/1/highscorelist", 200, "1=200");
        assertGet("/admin?check=sessions", 200, db.outputSessionTable());
        assertGet("/admin?check=users", 200, db.outputUserTable());
        assertGet("/admin?check=boards", 200, db.outputBoardTable());
    }

    private String assertGet(String path, int expectedCode)throws IOException {
        String fullPath = "http://" + address + ":" + port + path;
        URL url = new URL(fullPath);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        int responseCode = connection.getResponseCode();
        String responseBody = stringFromStream(getResponseByCode(connection));
//        LOGGER.info("URL: " + fullPath);
//        LOGGER.info("Code: " + responseCode);
//        LOGGER.info("Response: " + responseBody);
        assertEquals(expectedCode, responseCode);
        return responseBody;
    }

    private void assertGet(String path, int expectedCode, String expectedResponseBody)throws IOException {
        String responseBody = assertGet(path, expectedCode);
        assertEquals(expectedResponseBody.trim(), responseBody);
    }

    private void assertPost(String path, String body, int expectedCode)throws IOException {
        String fullPath = "http://" + address + ":" + port + path;
        URL url = new URL(fullPath);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        OutputStream osOfConnection = connection.getOutputStream();
        if(osOfConnection != null)
            osOfConnection.write(body.getBytes(CHARSET));
        int responseCode = connection.getResponseCode();
//        LOGGER.info("URL: " + fullPath.toString());
//        LOGGER.info("Code: " + responseCode);
        assertEquals(expectedCode, responseCode);

    }

    private InputStream getResponseByCode(HttpURLConnection connection)throws IOException {
        if(connection.getResponseCode() <= 200)
            return connection.getInputStream();
        else
            return connection.getErrorStream();

    }
}
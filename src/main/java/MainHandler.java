package main.java;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Map;

import static main.java.Utils.stringFromStream;
//import java.util.concurrent.ConcurrentHashMap;

public class MainHandler implements HttpHandler {
    private DataTables db;
    private Map<String, String> analysedURI;
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

    public MainHandler(DataTables db) {
        super();
        this.db = db;
        this.analysedURI = new HashMap<String, String>();
    }

    private void analyseURI(String path, String query) {
        this.analysedURI.clear();
        if (path != null &&
                !path.isEmpty()) {
            String[] subPath = path.split("/");
            if (subPath.length == 3 &&
                   subPath[0].length() < 1 &&
                    subPath[1].matches(Utils.REGEX_NUM) &&
                    (!subPath[2].isEmpty())) {

                if (subPath[2].equalsIgnoreCase("login")) {
                    this.analysedURI.put("userId", subPath[1]);
                    this.analysedURI.put("route", "login");

                } else if (subPath[2].equalsIgnoreCase("score") &&
                        query != null &&
                        !query.isEmpty()) {
                    String[] subQuery = query.split("&");
                    for (String sQ : subQuery) {
                        String pair[] = sQ.split("=");
                        if ((pair.length == 2) &&
                                pair[0].equalsIgnoreCase("sessionkey")) {
                            this.analysedURI.put("levelId", subPath[1]);
                            this.analysedURI.put("route", "score");
                            this.analysedURI.put("sessionKey", pair[1]);

                        }
                    }

                } else if (subPath[2].equalsIgnoreCase("highscorelist")) {
                    this.analysedURI.put("levelId", subPath[1]);
                    this.analysedURI.put("route", "highScoreList");

                }

            }
        }
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String query = httpExchange.getRequestURI().getQuery();
            analyseURI(path, query);
//            LOGGER.info("Path: " + path);
//            LOGGER.info("Query: " + query);
//            LOGGER.info("UserId: " + analysedURI.get("userId"));
//            LOGGER.info("LevelId: " + analysedURI.get("levelId"));
//            LOGGER.info("Route: " + analysedURI.get("route"));
//            LOGGER.info("SessionKey: " + analysedURI.get("sessionKey"));

            String route;
            if (analysedURI.containsKey("route"))
                route = analysedURI.get("route");
            else route = "welcome";
            switch (route) {
                case "login":
                    login(httpExchange);
                    break;
                case "score":
                    score(httpExchange);
                    break;
                case "highScoreList":
                    highScoreList(httpExchange);
                    break;

                default:
                    welcome(httpExchange);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void welcome(HttpExchange httpExchange) throws Exception {
        String response = "Welcome!";
        httpExchange.sendResponseHeaders(200, 0);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes(Utils.CHARSET));
        os.close();
    }

    private void login(HttpExchange httpExchange) throws Exception {
        if ("GET".equals(httpExchange.getRequestMethod())) {
            int userId = Integer.valueOf(analysedURI.get("userId"));
            if (!db.userIsExisted(userId)) {
                db.createUser(userId);
            }
            String sessionId = db.createSession(userId);
            if (sessionId != null && !sessionId.isEmpty()) {
//                LOGGER.info("UserId: " + userId);
//                LOGGER.info("SessionId: " + sessionId);
                httpExchange.sendResponseHeaders(200, 0);
                OutputStream os = httpExchange.getResponseBody();
                os.write(sessionId.getBytes(Utils.CHARSET));
                os.close();
            } else {
                LOGGER.info("Error 500: Failed to create session");
                httpExchange.sendResponseHeaders(500, -1);
            }

        } else {
            LOGGER.info("Error 403: Method not allowed!");
            httpExchange.sendResponseHeaders(403, -1);
        }
    }

    private void score(HttpExchange httpExchange) throws Exception {
        if ("POST".equals(httpExchange.getRequestMethod())) {
            int levelId = Integer.valueOf(analysedURI.get("levelId"));
            String sessionKey = analysedURI.get("sessionKey");
//            LOGGER.info("LevelId: " + levelId);
//            LOGGER.info("SessionKey: " + sessionKey);

            if (db.sessionIsValid(sessionKey)) {
                Headers requestHeaders = httpExchange.getRequestHeaders();
                String bodyString = stringFromStream(httpExchange.getRequestBody());
//                LOGGER.info("Request Body: " + bodyString);
                if (bodyString.matches(Utils.REGEX_NUM)) {
                    int score = Integer.valueOf(bodyString);
                    int userId = db.getUserIdBySession(sessionKey);
                    if (db.updateUserScore(userId, levelId, score)) {
                        if (db.updateHighScoreBoard(userId, levelId)) {
                            httpExchange.sendResponseHeaders(200, -1);

                        } else {
                            LOGGER.info("Error 500: Failed to update high score list");
                            httpExchange.sendResponseHeaders(500, -1);
                        }

                    } else {
                        LOGGER.info("Error 500: Failed to post score");
                        httpExchange.sendResponseHeaders(500, -1);

                    }

                } else {
                    LOGGER.info("Error 403: Request body illegal");
                    httpExchange.sendResponseHeaders(403, -1);
                }
            } else {
                LOGGER.info("Error 403: Session key invalid");
                httpExchange.sendResponseHeaders(403, -1);
            }

        } else {
            LOGGER.info("Error 403: Method not allowed");
            httpExchange.sendResponseHeaders(403, -1);
        }
    }

    private void highScoreList(HttpExchange httpExchange) throws Exception {
        if ("GET".equals(httpExchange.getRequestMethod())) {
            String response = "";
            int levelId = Integer.valueOf(analysedURI.get("levelId"));
//            LOGGER.info("LevelId: " + levelId);
            if (db.highScoreBoardIsExisted(levelId)) {
                response = db.getHighScoreBoard(levelId);
            }
            httpExchange.sendResponseHeaders(200, 0);
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes(Utils.CHARSET));
            os.close();
        } else {
            LOGGER.info("Error 403: Method not allowed!");
            httpExchange.sendResponseHeaders(403, -1);
        }
    }
}

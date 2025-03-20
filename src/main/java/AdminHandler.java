package main.java;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

public class AdminHandler implements HttpHandler {
    private DataTables db;
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

    public AdminHandler(DataTables db) {
        super();
        this.db = db;
    }
    private String getCheck(String query){
        String res = "";
        if (query != null &&
                !query.isEmpty()) {
            String[] subQuery = query.split("&");
            for (String sQ : subQuery) {
                String pair[] = sQ.split("=");
                if ((pair.length == 2) &&
                        pair[0].equalsIgnoreCase("check") &&
                        pair[1] != null &&
                        !pair[1].isEmpty())
                    res = pair[1];
            }
        }
        return res;
    }
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try{
            String query = httpExchange.getRequestURI().getQuery();
            String check = getCheck(query);
//            LOGGER.info("Query: " + query);
//            LOGGER.info("Check: " + check);

            if (check == null || check.isEmpty())
                check = "index";
            switch (check) {
                case "sessions":
                    checkSessions(httpExchange);
                    break;
                case "users":
                    checkUsers(httpExchange);
                    break;
                case "boards":
                    checkBoards(httpExchange);
                    break;
                default:
                    index(httpExchange);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void index(HttpExchange httpExchange) throws Exception{
        String response = "Admin!";
        httpExchange.sendResponseHeaders(200, 0);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes(Utils.CHARSET));
        os.close();


    }

    private void checkSessions(HttpExchange httpExchange) throws Exception{
        String response = db.outputSessionTable();
        httpExchange.sendResponseHeaders(200, 0);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes(Utils.CHARSET));
        os.close();


    }

    private void checkUsers(HttpExchange httpExchange) throws Exception{
        String response = db.outputUserTable();
        httpExchange.sendResponseHeaders(200, 0);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes(Utils.CHARSET));
        os.close();

    }

    private void checkBoards(HttpExchange httpExchange) throws Exception{
        String response = db.outputBoardTable();
        httpExchange.sendResponseHeaders(200, 0);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes(Utils.CHARSET));
        os.close();

    }

}

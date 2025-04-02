package main.java.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.java.DataTables;
import main.java.Utils;

import java.io.IOException;
import java.io.OutputStream;
//import java.util.logging.Logger;

public class AdminHandler implements HttpHandler {
    private DataTables db;
//    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

    public AdminHandler(DataTables db) {
        super();
        this.db = db;
    }

    private String getCheck(String query) {
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
        try {
            String query = httpExchange.getRequestURI().getQuery();
            String check = getCheck(query);
//            LOGGER.info("Query: " + query);
//            LOGGER.info("Check: " + check);

            if (check == null || check.isEmpty())
                check = "index";
            switch (check) {
                case "sessions":
                    check(httpExchange, db.outputSessionTable());
                    break;
                case "users":
                    check(httpExchange, db.outputUserTable());
                    break;
                case "boards":
                    check(httpExchange, db.outputBoardTable());
                    break;
                default:
                    check(httpExchange, "Admin!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void check(HttpExchange httpExchange, String response) throws Exception {
        httpExchange.sendResponseHeaders(200, 0);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes(Utils.CHARSET));
        os.close();
    }
}

package main.java;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;


import com.sun.net.httpserver.HttpServer;
import main.java.handlers.AdminHandler;
import main.java.handlers.MainHandler;


public class Server {
    private final static String address = "localhost";
    private final static int port = 8088;
    private final static int capacity = 1000;

    public static void main(String[] args) throws IOException {
        DataTables db = new DataTables();
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(address, port),0);
        httpServer.createContext("/",new MainHandler(db));
        httpServer.createContext("/admin",new AdminHandler(db));
        httpServer.setExecutor(Executors.newFixedThreadPool(capacity));
        httpServer.start();
    }
}

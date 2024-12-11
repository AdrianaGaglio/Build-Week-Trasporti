package server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class server {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        server.createContext("/rivenditori", new HandleRivenditori());
        server.createContext("/biglietti", new HandleBiglietti());
        server.createContext("/prova", new ProvaHandler());

        // Avvia il server
        server.start();
        System.out.println("Server avviato su http://localhost:8000");
    }
}

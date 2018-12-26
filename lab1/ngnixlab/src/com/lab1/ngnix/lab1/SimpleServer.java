package com.lab1.ngnix.lab1;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleServer {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8383), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {

        private AtomicInteger roundRobinCounter = new AtomicInteger(0);
        private AtomicInteger hashCounter = new AtomicInteger(0);

        @Override
        public void handle(HttpExchange t) throws IOException {
            String methodName = t.getRequestMethod().toLowerCase();
            String requestURI = t.getRequestURI().toString();
            String response = "";
            int counterVal;
            if ("get".equalsIgnoreCase(methodName)){
                requestURI = requestURI.substring(1);
                boolean isSuccesfull = true;
                switch (requestURI) {
                    case "round-robin":
                        getRoundRobinCounterIncrement();
                        break;
                    case "round-robin/stat":
                        counterVal=roundRobinCounter.get();
                        response = String.valueOf(counterVal);
                        break;
                    case "hash":
                        getHashCounterIncrement();
                        break;
                    case "hash/stat":
                        counterVal=hashCounter.get();
                        response = String.valueOf(counterVal);
                        break;
                    default:
                        isSuccesfull = false;
                        break;
                }
                if (isSuccesfull) {
                    t.sendResponseHeaders(200, response.length());

                }
                else {
                    t.sendResponseHeaders(500,response.length());
                }
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }

        }

        private int getRoundRobinCounterIncrement(){
            return roundRobinCounter.incrementAndGet();
        }

        private int getHashCounterIncrement(){
            return hashCounter.incrementAndGet();
        }
    }
}






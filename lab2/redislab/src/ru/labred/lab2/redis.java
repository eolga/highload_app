package ru.labred.lab2;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Protocol;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;


public class redis {
    public static void main(String[] args) throws Exception {
        int isLocalhost = args.length>0?1:0;
        HttpServer server = HttpServer.create(new InetSocketAddress(8383), 0);
        server.createContext("/", new MyHandler(getJedis(isLocalhost)));
        server.setExecutor(null); // creates a default executor
        server.start();

    }

    public static Jedis getJedis(int isLocalhost){
        JedisShardInfo jedisShardInfo;
        Jedis jedis = null;
        if (isLocalhost==1) {
            jedis = new Jedis(new JedisShardInfo("localhost",Protocol.DEFAULT_PORT));
            return jedis;
        }
        String baseIp = "172.17.0.";
        String newIp ="";
        for (int i=1;i<10;i++){
            newIp ="";
            try {
                newIp=baseIp.concat(String.valueOf(i));
                jedis = new Jedis(new JedisShardInfo(newIp,Protocol.DEFAULT_PORT));
                String val = jedis.get("round-robin");
                if (val.length()>0) {
                    return jedis;
                }
            } catch (Exception e){

            }
        }
        System.out.println(newIp);
        return jedis;
    }


    static class MyHandler implements HttpHandler {

        public MyHandler(Jedis jedis) {
            this.jedis = jedis;
        }

        private Jedis jedis;// = new Jedis(new JedisShardInfo("redis",Protocol.DEFAULT_PORT));

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
                        incrementValue("round-robin");
                        break;
                    case "round-robin/stat":
                        counterVal=getValue("round-robin");
                        response = String.valueOf(counterVal);
                        break;
                    case "hash":
                        incrementValue("hash");
                        break;
                    case "hash/stat":
                        counterVal=getValue("hash");
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

        private void incrementValue(String key){
            jedis.incr(key);
        }

        private int getValue(String key){
            return Integer.parseInt(jedis.get(key));
        }
    }

}
package bgu.spl181.net.impl;


import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.srv.bidi.ConnectionHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



    public class ConnectionsToServer<T> implements Connections<T> {
        private Map<Integer,ConnectionHandler<T>> CONNECTIONS = new ConcurrentHashMap<>();


        @Override
        public boolean send(int connectionId, T msg) {
            boolean exists = CONNECTIONS.containsKey(connectionId);
            synchronized (msg){
                if(exists){
                    CONNECTIONS.get(connectionId).send(msg);
                }
            }
            return exists;
        }

        @Override
        public void broadcast(T msg) {
            for(Integer key : CONNECTIONS.keySet()) CONNECTIONS.get(key).send(msg);
        }

        @Override
        public void disconnect(int connectionId) {
            CONNECTIONS.remove(connectionId);
        }

        public void addConnectionHnadler(Integer ID ,  ConnectionHandler<T> newBCH){
            if(!CONNECTIONS.containsKey(ID)) CONNECTIONS.put(ID,newBCH);
        }

        public void remove(Integer id){CONNECTIONS.remove(id); }

    }




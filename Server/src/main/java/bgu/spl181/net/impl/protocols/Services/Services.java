package bgu.spl181.net.impl.protocols.Services;

import bgu.spl181.net.impl.protocols.ShareDataManager;
import bgu.spl181.net.impl.protocols.User;
import java.util.Queue;

public abstract class Services extends ShareDataManager {
    protected String permission;
    protected int user_id;
    protected Queue<String> message_as_queue;

    public Services(Integer id , String _permission,Queue<String> message){
        permission = _permission;
        user_id = id;
        message_as_queue = message;
    }

    protected void sendMessage(Integer id,String msg){
        connections.send(id,msg);
    }

    protected void sendBroacast(String message){
        connections.broadcast(message);
    }

    abstract void execute();
}

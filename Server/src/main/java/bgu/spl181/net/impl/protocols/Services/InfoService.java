package bgu.spl181.net.impl.protocols.Services;



import bgu.spl181.net.impl.protocols.Movie;

import java.util.Queue;

public class InfoService extends Services{

    public InfoService(Integer id ,Queue<String> message , String permission ){
        super(id,permission,message);
        execute();
    }

    @Override
    public void execute() {
        if(!all_login_users.isEmpty() && all_login_users.containsKey(user_id)){
            if(!message_as_queue.isEmpty()){
                String movieName = message_as_queue.poll();
                Movie movieInfo = getMovie(movieName);
                if (movieInfo != null) {
                    String movieInf = movieInfo.myInfo();
                    releaseMovie(movieName);
                    sendMessage(user_id, "ACK info " + movieInf);
                }else sendMessage(user_id, "ERROR request info failed");
            }else sendMessage(user_id, "ACK info" +  createMoviesNameString());
        }else sendMessage(user_id,"ERROR request info failed");

    }


}


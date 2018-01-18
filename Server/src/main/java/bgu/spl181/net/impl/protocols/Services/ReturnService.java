package bgu.spl181.net.impl.protocols.Services;



import bgu.spl181.net.impl.protocols.Movie;
import bgu.spl181.net.impl.protocols.User;

import java.util.Queue;

public class ReturnService extends Services{

    public ReturnService(Integer id ,Queue<String> message , String permission ){
        super(id,permission,message);
        execute();
    }

    @Override
    public void execute(){
        if(!message_as_queue.isEmpty() && all_login_users.containsKey(user_id)){
            String movieName = message_as_queue.poll();
            User user = all_login_users.get(user_id);
            Movie returnMovie = getMovie(movieName);
            if(returnMovie != null){                                        //If not null it means that the movie exists in the system
               if(user.haveTheMovie(movieName)){
                   user.returnIt(movieName);
                   returnMovie.returnMe();
                   sendMessage(user_id,"ACK return "+"\""+movieName+"\""+" success");
                   sendBroacast("BROADCAST movie " + "\"" + movieName + "\""+" "+returnMovie.availableAmount()+" "+returnMovie.getPrice());
                   updateMoviesJson();
                   updateUsersJson();
                   releaseMovie(movieName);
               }else{
                   sendMessage(user_id,"ERROR request return failed");
                   releaseMovie(movieName);
               }
            }else{
                sendMessage(user_id,"ERROR request return failed");
            }
        }else sendMessage(user_id,"ERROR request return failed");

    }
}

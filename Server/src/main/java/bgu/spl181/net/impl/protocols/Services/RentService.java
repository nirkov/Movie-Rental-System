package bgu.spl181.net.impl.protocols.Services;


import bgu.spl181.net.impl.protocols.Movie;
import bgu.spl181.net.impl.protocols.User;

import java.util.Queue;

public class RentService extends Services{

    public RentService(Integer id ,Queue<String> message , String permission ){
        super(id,permission,message);
        execute();
    }

    @Override
    public void execute() {
        if(!message_as_queue.isEmpty() && all_login_users.containsKey(user_id)){
            String movieName = message_as_queue.poll();
            Movie toRent = getMovie(movieName);
            if(toRent != null){
                User tempUser = getSpecificUser(user_id);
                if (isTheMovieInSystem(movieName)                        //If the movie exists in the system
                         &&(toRent.availableAmount() > 0                 //and there are available copies of the movie
                         &&tempUser.getBalance() >= toRent.getPrice()    //and the user has enough money to rent the movie
                         &&!tempUser.haveTheMovie(movieName)               //and the user does not already have the movie
                         &&!toRent.isBannedIn(tempUser.getCountry()))) {   //and the movie is not blocked in the country from which the user logged in
                    toRent.rentMe();
                    tempUser.addBalance(-toRent.getPrice());
                    tempUser.rentIt(toRent.getName(), toRent.getID());
                    sendMessage(user_id, "ACK rent " + "\"" + movieName + "\"" + " success");
                    updateMoviesJson();
                    sendBroacast("BROADCAST movie " + "\"" + movieName + "\""+" "+toRent.availableAmount()+" "+toRent.getPrice());
                    releaseMovie(movieName);
                    updateUsersJson();
                }else{
                    sendMessage(user_id,"ERROR request rent failed");
                    releaseMovie(movieName);
                }
            }else sendMessage(user_id,"ERROR request rent failed");
        }else sendMessage(user_id,"ERROR request rent failed");
    }


}


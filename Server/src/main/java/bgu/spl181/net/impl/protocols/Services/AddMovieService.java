package bgu.spl181.net.impl.protocols.Services;


import bgu.spl181.net.impl.protocols.Movie;

import java.util.LinkedList;
import java.util.Queue;

public class AddMovieService extends Services{

    public AddMovieService(Integer id ,Queue<String> message , String permission ){
        super(id,permission,message);
        execute();
    }


    @Override
    public void execute() {
        if (permission.equals("admin") && all_login_users.containsKey(user_id)) {                               //If the user is admin he try add new movie
            int amount = 0;
            int price = 0;
            String movieName = message_as_queue.poll();
            try {
                amount = Integer.parseInt(message_as_queue.poll());
                price = Integer.parseInt(message_as_queue.poll());
            } catch (NumberFormatException e) {
                sendMessage(user_id, "ERROR request addmovie failed");
                return;
            }
            if (amount > 0 && price > 0) {
                Movie newMovie = new Movie(id_for_movie()   // create new movie with parameters which received
                        , movieName
                        , price
                        , new LinkedList<>(message_as_queue)
                        , amount);
                newMovie = addMovie(newMovie);                          //try to add it - if the movie exists in the system the function return null
                if (newMovie != null) {                                 //if not null, the new movie added to the system
                    updateMoviesJson();                                 //update the json with a new movie
                    releaseMovie(newMovie.getName());                   //release the movie
                    sendMessage(user_id, "ACK addmovie " + "\"" + movieName + "\"" + " success");
                    sendBroacast("BROADCAST movie " + "\"" + movieName + "\"" + " " + String.valueOf(amount) + " " + String.valueOf(price));
                } else sendMessage(user_id, "ERROR request addmovie failed");
            } else sendMessage(user_id, "ERROR request addmovie failed");
        }else sendMessage(user_id, "ERROR request addmovie failed");
    }

}





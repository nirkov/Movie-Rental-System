package bgu.spl181.net.impl.protocols.Services;

import bgu.spl181.net.impl.protocols.Movie;

import java.util.Queue;

public class RemovMovieService extends Services {

    public RemovMovieService(Integer id, Queue<String> message, String permission) {
        super(id, permission, message);
        execute();
    }

    @Override
    public void execute() {
        if (permission.equals("admin") && all_login_users.containsKey(user_id)) {
            String movieName = message_as_queue.poll();
            Movie toRemove = getMovie(movieName);
            if (toRemove != null) {
                if (toRemove.availableAmount() == toRemove.getNumOfCopies()) {
                    sendMessage(user_id, "ACK remmovie " + "\"" + movieName + "\"" + " success");
                    sendBroacast("BROADCAST movie " + "\"" + movieName + "\" removed");
                    removeMovie(movieName);
                    updateMoviesJson();
                } else{
                    sendMessage(user_id, "ERROR request remmovie failed");
                    releaseMovie(movieName);
                }
            } else sendMessage(user_id, "ERROR request remmovie failed");
        } else sendMessage(user_id, "ERROR request remmovie failed");
    }

}


















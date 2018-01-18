package bgu.spl181.net.impl.protocols.Services;


import bgu.spl181.net.impl.protocols.Movie;

import java.util.Queue;

public class ChangePriceService extends Services{

    public ChangePriceService(Integer id ,Queue<String> message , String permission ){
        super(id,permission,message);
        execute();

    }

    @Override
    public void execute() {
        if(permission.equals("admin") && all_login_users.containsKey(user_id)){
            String movieName = message_as_queue.poll();
            int newPrice = 0;
            try{
                newPrice = Integer.parseInt(message_as_queue.poll());
            }catch (NumberFormatException e){
                sendMessage(user_id,"ERROR request changeprice failed");
            }
            Movie movie = getMovie(movieName);
            if(movie!=null){
                if(newPrice > 0){
                    movie.changePrice(newPrice);
                    updateMoviesJson();
                    sendMessage(user_id,"ACK changeprice "+"\""+movie.getName()+"\""+" success");
                    sendBroacast("BROADCAST movie "+"\""+movie.getName()+"\""+" "+movie.availableAmount()+" "+movie.getPrice());
                    releaseMovie(movieName);
                }else {
                    sendMessage(user_id,"ERROR request changeprice failed");
                    releaseMovie(movieName);
                }
            }else{
                sendMessage(user_id,"ERROR request changeprice failed");
            }
        }else sendMessage(user_id,"ERROR request changeprice failed");
    }
}

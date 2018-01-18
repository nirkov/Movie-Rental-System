package bgu.spl181.net.impl.protocols;

import bgu.spl181.net.impl.protocols.Services.*;

import java.util.Queue;

public class MovieRentalProtocol<T> {

    public MovieRentalProtocol(){
    }

    public void MovieRentalRequestsHandler(Integer id ,Queue<String> message , String permission){
        String requestType = message.poll();


        switch(requestType){
            case "REGISTER":
                new registerService(id , message,permission);
                break;
            case "balance":
                switch(message.poll()){
                      case "info":
                          new BalanceCheckService(id , message , permission);
                          break;
                      case "add":
                          new BalanceAddService(id , message , permission);
                          break;
                }
                break;
            case "info":
                new InfoService(id , message , permission);
                break;
            case "rent":
                new RentService(id , message , permission);
                break;
            case "return":
                new ReturnService(id , message , permission);
                break;
            case "addmovie":
                new AddMovieService(id , message , permission);
                break;
            case "remmovie":
                new RemovMovieService(id , message , permission);
                break;
            case "changeprice":
                new ChangePriceService(id , message , permission);
                break;
        }
    }




}

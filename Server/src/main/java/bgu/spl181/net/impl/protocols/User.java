package bgu.spl181.net.impl.protocols;

import java.util.LinkedList;

public class User {
    private String username;
    private String type;
    private String password;
    private String country;
    private LinkedList<MoviePairInfo> movies;
    private int balance;



    public User( String _username,String _password , String _country , String _type){
        username = _username;
        type = _type;
        password = _password;
        country = _country;
        movies = new LinkedList<>();
        balance = 0;
     
    }

    public int addBalance(int toAdd) {
        balance = balance + toAdd;
        return balance;
    }

    public boolean haveTheMovie(String movieName){
        boolean haveIt = false;
        for(MoviePairInfo m : movies){
            if(m.name.equals(movieName)){
                haveIt = true;
                break;
            }
        }
        return haveIt;
    }

    public void rentIt(String movieName , int id){ movies.add(new MoviePairInfo(movieName,id)) ; }

    //getter

    public String getType(){ return type; }

    public int getBalance() { return balance; }

    public String getCountry(){ return country; }

    public String getPassword() { return password; }

    public String getName() { return username; }

    public void returnIt(String movieName) {
        for (MoviePairInfo m : movies) {
            if (m.name.equals(movieName)) {
                movies.remove(m);
                break;
            }
        }
    }

    //Movie pair private class
    private class MoviePairInfo {
        private String id;
        private String name;
        
        public MoviePairInfo(String _name, int _id) {
            id = String.valueOf(_id); 
            name = _name;   
        }
    }
}



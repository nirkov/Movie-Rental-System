package bgu.spl181.net.impl.protocols;

import java.util.LinkedList;


public class Movie {
    private final int id;
    private final String name;
    private  LinkedList<String> bannedCountries;
    private int price;
    private int availableAmount;
    private int totalAmount;
    



    public Movie(int _id , String _name , int _price , LinkedList<String> _bannedCountries , int _sumOfCopy){
        id = _id;
        name = _name;
        price = _price;
        bannedCountries = new LinkedList();
        bannedCountries.addAll(_bannedCountries);
        availableAmount = _sumOfCopy;
        totalAmount = _sumOfCopy;
       
    }

    public String myInfo(){
        return (name +" "+ price+" "+ availableAmount +" "+ arrayToString());
    }

    public boolean isBannedIn(String country){
        boolean banned = false;
        for(String s : bannedCountries){
            if(s.equals(country)){
                banned = true;
                break;
            }
        }
        return banned;
    }

    public void rentMe(){ availableAmount--; }

    public void returnMe(){availableAmount ++; }

    public void changePrice(int amount){price = amount; }

    /**
     * The function accepts an array and returns a string
     * @return A string with all country names where the movie is blocked
     */
    private String arrayToString(){
        String bannedCountry = "";
        if(!bannedCountries.isEmpty()){
            for(String s : bannedCountries) bannedCountry = bannedCountry +" "+"\""+s+"\",";
            bannedCountry = bannedCountry.substring(0 , bannedCountry.length()-1);
        }
        return bannedCountry;
    }

    //getter
    public int getID() { return id; }

    public int availableAmount() { return availableAmount; }

    public int getPrice() { return price; }

    public String getName() { return name; }

    public int getNumOfCopies(){ return totalAmount;}
}

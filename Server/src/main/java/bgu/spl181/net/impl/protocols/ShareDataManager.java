package bgu.spl181.net.impl.protocols;
import bgu.spl181.net.impl.ConnectionsToServer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class ShareDataManager {

    private final static ReadWriteLock MOVIE_RW_LOCK       = new ReentrantReadWriteLock();
    private final static ReadWriteLock JSON_RW_LOCK_MOVIES = new ReentrantReadWriteLock();
    private final static ReadWriteLock JSON_RW_LOCK_USERS  = new ReentrantReadWriteLock();

    private static MovieJsonGenerator movie_json_generator = MovieJsonGenerator.getInstance();
    private static UserJsonGenerator  user_json_generator  = UserJsonGenerator.getInstance();

    protected static ConcurrentHashMap<String, User>    all_users_in_system = new ConcurrentHashMap();
    protected static ConcurrentHashMap<Integer, User>   all_login_users     = new ConcurrentHashMap();
    protected static ConcurrentHashMap<String, Movie>   all_movie           = new ConcurrentHashMap();
    protected static ConcurrentHashMap<String, Boolean> locked_movie        = new ConcurrentHashMap();
    
    private static ArrayList<Integer> moviesId = new ArrayList<>();

    protected static int movie_id = 0;
    private   static VersionMonitor VM = new VersionMonitor();
    protected static ConnectionsToServer connections;

    public void Loading_Initial_Information(String pathUser, String pathMovie) {
        LinkedList<Movie> reloadMovies = movie_json_generator.setPathReloadData(pathMovie);
        LinkedList<User>  reloadUsers  = user_json_generator.setPathReloadData(pathUser);
        
        reloadMovies.forEach((movie) -> {
            all_movie.put(movie.getName(),movie);
            locked_movie.put(movie.getName(),false);
            moviesId.add(movie.getID());
            if(movie.getID() > movie_id){
                movie_id = movie.getID();
            }
        });
        
        reloadUsers.forEach((user) -> {
            all_users_in_system.put(user.getName(),user);
        });
        
        Collections.sort(moviesId);
        Collections.reverse(moviesId);
    }

    public void ShareDataManager(ConnectionsToServer _connections){
            connections = _connections;
        }


    //These functions change the information and therefore JSON need to be updated

    protected void updateMoviesJson(){
            JSON_RW_LOCK_MOVIES.writeLock().lock();
            movie_json_generator.write_to_movies(all_movie);
            JSON_RW_LOCK_MOVIES.writeLock().unlock();
        }

    protected void updateUsersJson(){
            JSON_RW_LOCK_USERS.writeLock().lock();
            user_json_generator.write_to_user(all_users_in_system);
            JSON_RW_LOCK_USERS.writeLock().unlock();
        }

    public boolean tryLogIn(Integer ID , Queue<String> messageQueue){
            String userName = messageQueue.poll();
            String password = messageQueue.poll();
            if(all_users_in_system.containsKey(userName) &&
                    !all_login_users.containsKey(ID) &&
                    all_users_in_system.get(userName).getPassword().equals(password)){

                all_login_users.put(ID,all_users_in_system.get(userName));
                return true;
            }
            return false;
        }

    public boolean tryLogOut(Integer ID){
            if(all_login_users.containsKey(ID)){
                all_login_users.remove(ID);
                VM.inc();                                       
                return true;
            }
            return false;
        }

    public void removeConnect(int id){
        connections.remove(id);
    }


    //Getter functions

    protected String getPermission(int id){
        if( all_login_users.containsKey(id)){
            return all_login_users.get(id).getType();
        }else return "None";
    }

    protected boolean isTheMovieInSystem(String name){
            boolean contain = false;
            MOVIE_RW_LOCK.readLock().lock();
            if(!all_movie.isEmpty()){
                contain = all_movie.containsKey(name);
            }
            MOVIE_RW_LOCK.readLock().unlock();
            return contain;
        }

    protected User getSpecificUser(int id){ return all_login_users.get(id); }

    protected boolean isLogin(Integer id){ return all_login_users.containsKey(id) ; }


    /**
     * This functions create a String of movies name in shape like this -
     * "movie1","movie2",..."movie-n".
     */
        protected String createMoviesNameString(){
            MOVIE_RW_LOCK.writeLock().lock();
            Set<String> s = all_movie.keySet();
            String names = " ";
            for(String k : s) names = names + "\""+k+"\" ";
            names = names.substring(0,names.length()-1);
            MOVIE_RW_LOCK.writeLock().unlock();
            return names;
        }

        protected Movie getMovie(String name){
            MOVIE_RW_LOCK.writeLock().lock();                 //Locked the name until the thread take the movie or get sleep
            Movie temp = null;
            if(all_movie.containsKey(name)){                  //if the movie exists in the system
                if(!locked_movie.get(name)){                  //and if there is no thread that currently owns the movie
                    temp =  all_movie.get(name);              //this thread take the movie
                    locked_movie.replace(name,false,true);    //locked the movie
                    MOVIE_RW_LOCK.writeLock().unlock();       //and release the writelock key
                }else{
                    MOVIE_RW_LOCK.writeLock().unlock();       //else if the movie exists but locked
                    try{
                        VM.wait(VM.getVersion()+1);           //the tread go to sleep until the movie will release
                    }catch (InterruptedException e){}
                    temp = getMovie(name);                    //and when he wake up he try take the movie again
                }
            }else{
                MOVIE_RW_LOCK.writeLock().unlock();           //else if the movie not exists we return null
                return temp;    //return null
            }
         return temp;    //return movie
        }

        protected void releaseMovie(String movieName) {
            if(!locked_movie.isEmpty()){
                locked_movie.replace(movieName,true,false);
            }
            VM.inc();
        }

        protected Movie addMovie(Movie newMovie) {
            synchronized (newMovie) {
                if (!all_movie.containsKey(newMovie.getName())) {
                    locked_movie.put(newMovie.getName(), true);
                    all_movie.put(newMovie.getName(), newMovie);
                    return newMovie;
                }else return null;
            }
        }

        protected void removeMovie(String movieName){
            MOVIE_RW_LOCK.writeLock().lock();
            if(all_movie.get(movieName).getID() == movie_id){
                all_movie.remove(movieName);
                locked_movie.remove(movieName);
                moviesId.remove(0);
                if(!moviesId.isEmpty()){
                    movie_id = moviesId.get(0);
                }else movie_id = 0;
            }else{
                int id = all_movie.get(movieName).getID();
                all_movie.remove(movieName);
                locked_movie.remove(movieName);
                for(Integer k : moviesId){
                    if(id == k){
                        moviesId.remove(k);
                        break;
                    }
                }
            }
            MOVIE_RW_LOCK.writeLock().unlock();
            VM.inc();
        }

        protected int id_for_movie(){
            movie_id++;
            if(!moviesId.isEmpty()){
                moviesId.add(0,movie_id);
            }else moviesId.add(movie_id);
            return movie_id;
        }
}



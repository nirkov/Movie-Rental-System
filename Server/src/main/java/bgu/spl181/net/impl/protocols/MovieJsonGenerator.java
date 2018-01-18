package bgu.spl181.net.impl.protocols;


import com.google.gson.*;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;


public class MovieJsonGenerator {
    private static MovieJsonGenerator JSON_GENERATOR = new MovieJsonGenerator();
    private String path = "";
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Integer.class, new IntegerAdapter())   //Configures Gson to serialize Integer to String
            .setPrettyPrinting()                                        //Configures Gson to output Json that fits in a page for pretty printing
            .serializeNulls()                                           //Configures Gson to serialize null field
            .create();


    public static MovieJsonGenerator getInstance() {
        return JSON_GENERATOR;
    }

    public LinkedList<Movie> setPathReloadData(String moviePath){
        this.path = moviePath;
        MoviesJ movies = null;
        try {
            FileReader reader = new FileReader(path);
            movies = gson.fromJson(reader, MoviesJ.class);
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movies.movies;
    }

    private MovieJsonGenerator() {}

    public void write_to_movies( ConcurrentHashMap<String , Movie> allMovies){
        MoviesJ movies = new MoviesJ(allMovies);
        try{
            FileWriter writer = new FileWriter(path);
            gson.toJson(movies , MoviesJ.class ,writer );
            writer.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }



    private class IntegerAdapter implements JsonSerializer<Integer> {
        @Override
        public JsonElement serialize(Integer num, Type type, JsonSerializationContext jsonSerializationContext) {
            String value = num.toString();
            return new JsonPrimitive(value);
        }
    }

    private class MoviesJ {
        private LinkedList<Movie> movies;
        public MoviesJ ( ConcurrentHashMap<String , Movie> allUser){
            movies = new LinkedList<>(allUser.values());
        }
    }



}


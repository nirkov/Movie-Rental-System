package bgu.spl181.net.impl.protocols;

import com.google.gson.*;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;


public class UserJsonGenerator {
    private static UserJsonGenerator JSON_GENERATOR = new UserJsonGenerator();
    private String path = "";

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Integer.class, new IntegerAdapter())   //Configures Gson to serialize Integer to String
            .setPrettyPrinting()                                        //Configures Gson to output Json that fits in a page for pretty printing
            .serializeNulls()                                           //Configures Gson to serialize null field
            .create();


    public static UserJsonGenerator getInstance() {
        return JSON_GENERATOR;
    }

    private UserJsonGenerator() {}

    public LinkedList<User> setPathReloadData(String usersPath){
        this.path = usersPath;
        UsersJ users = null;
        try {
            FileReader reader = new FileReader(path);
            users = gson.fromJson(reader, UsersJ.class);
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users.users;
    }

    public void write_to_user( ConcurrentHashMap<String , User> allUser) {
        UsersJ users = new UsersJ(allUser);
        try {
            FileWriter writer = new FileWriter(path);
            gson.toJson(users, UsersJ.class, writer);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
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


    private class UsersJ {
        private LinkedList<User> users;
        public UsersJ ( ConcurrentHashMap<String , User> allUser){
            users = new LinkedList<>(allUser.values());
        }
    }
}

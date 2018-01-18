package bgu.spl181.net.impl.protocols.Services;


import bgu.spl181.net.impl.protocols.User;

import java.util.Queue;

public class registerService  extends Services{


        public registerService(Integer id , Queue<String> message , String permission ){
            super(id,permission,message);
            execute();
        }


        @Override
        public void execute() {
            if (message_as_queue.size() == 3) {                                          //Only if contain name , password and country
                String userName = message_as_queue.poll();
                if(!all_users_in_system.containsKey(userName)){    //and if user name not exists in the system
                    String password = message_as_queue.poll();
                    String country =  message_as_queue.poll();
                    User newUser = new User(userName, password, country,"normal"); //create new normal user
                    all_users_in_system.put(userName ,newUser);                          //save is data
                    updateUsersJson();                                                   //updata json file data of client
                    sendMessage(user_id,"ACK registration succeeded");
                } else sendMessage(user_id,"ERROR registration failed");
            } else sendMessage(user_id,"ERROR registration failed");
        }

}

package bgu.spl181.net.impl.protocols.Services;


import java.util.Queue;

public class BalanceAddService extends Services{

    public BalanceAddService(Integer id ,Queue<String> message , String permission ){
        super(id,permission,message);
        execute();
    }


    @Override
    public void execute() {
        if(!message_as_queue.isEmpty() && all_login_users.containsKey(user_id)){
            try {
                int amount = Integer.parseInt(message_as_queue.peek());
                if(amount > 0){
                    updateUsersJson();
                    sendMessage(user_id,"ACK balance "+String.valueOf(addBalance(user_id,amount))+" added "+message_as_queue.poll());
                }else  sendMessage(user_id,"ERROR request balance failed");
            }catch(NumberFormatException e){
                sendMessage(user_id,"ERROR request balance failed");
            }
        }else sendMessage(user_id,"ERROR request balance failed");
    }



    protected int addBalance(Integer id , int toAdd){
        int newBalance = all_login_users.get(id).addBalance(toAdd);  //return new balance after added 'amount'
        return newBalance;
    }


}






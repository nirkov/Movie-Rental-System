package bgu.spl181.net.impl.protocols.Services;


import java.util.Queue;

public class BalanceCheckService extends Services {

    public BalanceCheckService(Integer id ,Queue<String> message , String permission ){
        super(id,permission,message);
        execute();
    }

    /**
     * return send to sendMessage : "ACK balnce 'user balance'
     */
    @Override
    public void execute() {
        if(all_login_users.containsKey(user_id)) {
            sendMessage(user_id,"ACK balance "+String.valueOf(all_login_users.get(user_id).getBalance()));
        }else sendMessage(user_id , "ERROR request balance failed");
    }
}

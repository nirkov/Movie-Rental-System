package bgu.spl181.net.impl.protocols;

import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.impl.ConnectionsToServer;

import java.util.Queue;

public class UserServiceTextBasedProtocol <T>  implements BidiMessagingProtocol<T> {
    private boolean terminate;
    private int USER_ID;
    private Connections<T> CONNECTIONS;
    private MovieRentalProtocol MRP;
    private static ShareDataManager SHARE_DATA_SERVICE;
    CommandInterpreter regularExpressionsCommand = new CommandInterpreter();


    public static void reloadShareData(ShareDataManager shareData) {
        SHARE_DATA_SERVICE = shareData;
    }

    @Override
    public void start(int connectionId, Connections<T> connections) {
        terminate = false;
        USER_ID = connectionId;
        CONNECTIONS = connections;
        MRP = new MovieRentalProtocol();
        SHARE_DATA_SERVICE.ShareDataManager((ConnectionsToServer) CONNECTIONS);
        regularExpressionsCommand = new CommandInterpreter();
    }

    @Override
    public void process(T message) {
        if (message != null) {
            String waitForServerAns = message.toString().trim().replaceAll("\\s{2,}", " ");
            Queue<String> messageQueue = regularExpressionsCommand.giveMeCommand(waitForServerAns);
            String typeCommand = messageQueue.peek();

            switch (typeCommand) {
                case "REGISTER":
                    MRP.MovieRentalRequestsHandler(USER_ID, messageQueue , "normal");
                    break;

                case "LOGIN":
                    messageQueue.poll();
                    if (messageQueue.size() == 2) {
                        if (SHARE_DATA_SERVICE.tryLogIn(USER_ID, messageQueue)) {
                            sendServerResponse("ACK", "login");
                        } else sendServerResponse("ERROR", "login");
                    } else sendServerResponse("ERROR", "login");
                    break;

                case "SIGNOUT":
                    messageQueue.poll();
                    if (messageQueue.isEmpty() & SHARE_DATA_SERVICE.tryLogOut(USER_ID)) {
                        terminate = true;
                        sendServerResponse("ACK", "signout");
                        SHARE_DATA_SERVICE.removeConnect(USER_ID);
                    } else sendServerResponse("ERROR", "signout");
                    break;

                case "REQUEST":
                    messageQueue.poll();
                        MRP.MovieRentalRequestsHandler(USER_ID, messageQueue , SHARE_DATA_SERVICE.getPermission(USER_ID));
                    break;

                default:
                    break;


            }

        }

    }

    @Override
    public boolean shouldTerminate() {
        return terminate;
    }

    public void sendServerResponse(String tryAns, String typeCommand) {
        String ans;
        if (tryAns.equals("ACK")) {
            ans = "succeeded";
        } else ans = "failed";
        CONNECTIONS.send(USER_ID, (T) (tryAns + " " + typeCommand + " " + ans));
    }

}


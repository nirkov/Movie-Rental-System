package bgu.spl181.net.impl.BBtpc;

import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.impl.LineMessageEncoderDecoder;
import bgu.spl181.net.impl.protocols.ShareDataManager;
import bgu.spl181.net.impl.protocols.UserServiceTextBasedProtocol;
import bgu.spl181.net.srv.Server;

import java.util.function.Supplier;

public class TPCMain {

    public static void main(String[] args){
          int port = 0;
          try {
             port = Integer.parseInt(args[0]);
          }catch(NumberFormatException e){

          }catch(NullPointerException e){}

        String path = System.getProperty("user.dir");
        String pathUsers = path + "/Database/Users.json";
        String pathMovies = path + "/Database/Movies.json";


        ShareDataManager shareData =  new ShareDataManager();
        shareData.Loading_Initial_Information(pathUsers,pathMovies);
        Supplier<MessageEncoderDecoder<String>> encdec = new Supplier<MessageEncoderDecoder<String>>() {
            @Override
            public MessageEncoderDecoder<String> get() {
                return new LineMessageEncoderDecoder();
            }


        };
        Supplier<BidiMessagingProtocol<String>> ProtocolSupplier = new Supplier<BidiMessagingProtocol<String>>() {
            @Override
            public BidiMessagingProtocol get() {
                UserServiceTextBasedProtocol p = new UserServiceTextBasedProtocol();
                p.reloadShareData(shareData);
                return p;
            }
        };

        Server<String> server = Server.threadPerClient(port, ProtocolSupplier, encdec);
        server.serve();

    }
}

package bgu.spl181.net.srv;
import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.impl.ConnectionsToServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

public abstract class BaseServer<T> implements Server<T> {

    private final int port;
    private final Supplier<BidiMessagingProtocol<T>>   protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    private ServerSocket sock;
    private Integer COUNTER_OF_ID;
    private ConnectionsToServer<T> CONNECTIONS;

    public BaseServer(
            int port,
            Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encdecFactory) {

        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
		this.sock = null;
		this.COUNTER_OF_ID = 0;
		this.CONNECTIONS = new ConnectionsToServer<>();
    }

    @Override
    public void serve() {
        try (ServerSocket serverSock = new ServerSocket(port)) {              //ServerSocket will throw an execption if it can't listen to this port
			System.out.println("Server started");
            this.sock = serverSock;                                           //just to be able to close - server<T> extends Closeable
            while (!Thread.currentThread().isInterrupted()) {
                // If the server successfully binds to its port the accept method will waits until a
                // client starts up and requests a connection on the host and port of this server
                Socket clientSock = serverSock.accept();
                // When a connection is requested and successfully established, the accept method
                // returns a new Socket object which is bound to the same local port and has its
                // remote address and remote port set to that of the client.
                new_client_accepted(clientSock);
            }
        } catch (IOException ex) {
        }

        System.out.println("server closed!!!");
    }

    @Override
    public void close() throws IOException {
		if (sock != null)
			sock.close();
    }

    protected abstract void execute(BlockingConnectionHandler<T>  handler);

    private void new_client_accepted( Socket clientSock){
        BidiMessagingProtocol temp = protocolFactory.get();
        BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<>(
                clientSock,
                encdecFactory.get(),
                temp);
        COUNTER_OF_ID ++;
        CONNECTIONS.addConnectionHnadler(COUNTER_OF_ID, handler);
        temp.start(COUNTER_OF_ID,CONNECTIONS);
        execute(handler);
    }

}

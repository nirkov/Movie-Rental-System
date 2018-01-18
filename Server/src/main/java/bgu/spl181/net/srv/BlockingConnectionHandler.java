package bgu.spl181.net.srv;

import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.MessagingProtocol;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.srv.bidi.ConnectionHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/**.
     The BlockingConnectionHandler is a runnable whose task is to handle a single connection.
     In order to receive and respond to messages, it uses a MessageEncoderDecoder and a
     MessagingProtocol. The first separates incoming bytes into complete messages while the
     second processes complete messages and produces responses. Responses are then encoded
     via the MessageEncoderDecoder before writing them to the socket.
 */


public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocol<T> protocol) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
    }

    @Override
    public void run() {
        try (Socket sock = this.sock) {/*just for automatic closing */
            int read;
            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());
            // read() method - read the next byte of data from the input stream. The value byte
            // is returned as an int in the range 0 to 255. If no byte is available
            // because the end of the stream has been reached, the value -1 is returned.
            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                     protocol.process(nextMessage);    //process of BidiMessagingProtocol
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    @Override
    public void send(T msg) {
        try {
            out.write(encdec.encode(msg));
            out.flush();
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

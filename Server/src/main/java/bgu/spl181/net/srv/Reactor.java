package bgu.spl181.net.srv;

import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.impl.ConnectionsToServer;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public class Reactor<T> implements Server<T> {

    private final int port;
    private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> readerFactory;
    private final ActorThreadPool pool;
    private Selector selector;
    private Integer COUNTER_OF_ID;
    private Thread selectorThread;
    private ConnectionsToServer<T> CONNECTIONS;
    private final ConcurrentLinkedQueue<Runnable> selectorTasks = new ConcurrentLinkedQueue<>();

    public Reactor(
            int numThreads,
            int port,
            Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> readerFactory) {

        this.COUNTER_OF_ID = 0;
        this.pool = new ActorThreadPool(numThreads);
        this.port = port;
        this.protocolFactory = protocolFactory;
        this.readerFactory = readerFactory;
        CONNECTIONS = new ConnectionsToServer<>();
    }

    @Override
    public void serve() {
	selectorThread = Thread.currentThread();
        try (Selector selector = Selector.open();   //try to create the selector
                ServerSocketChannel serverSock = ServerSocketChannel.open()) {
            this.selector = selector; //just to be able to close
            serverSock.bind(new InetSocketAddress(port));   //connect the server socket to this port
            serverSock.configureBlocking(false);    //non blocking connection handler
            serverSock.register(selector, SelectionKey.OP_ACCEPT); //Every time a user tries to connect, the selector will alert
			System.out.println("Server started");

            while (!Thread.currentThread().isInterrupted()) {
                selector.select();  //All requests received by clients so far are entered into selectedKeys
                runSelectionThreadTasks();
                for (SelectionKey key : selector.selectedKeys()) {
                    if (!key.isValid()) {
                        continue;
                    } else if (key.isAcceptable()) {
                        handleAccept(serverSock, selector);
                    } else {
                        handleReadWrite(key);
                    }
                }

                selector.selectedKeys().clear(); //clear the selected keys set so that we can know about new events

            }

        } catch (ClosedSelectorException ex) {
            //do nothing - server was requested to be closed
        } catch (IOException ex) {
            //this is an error
            ex.printStackTrace();
        }

        System.out.println("server closed!!!");
        pool.shutdown();
    }

    /*package*/ void updateInterestedOps(SocketChannel chan, int ops) {
        final SelectionKey key = chan.keyFor(selector);
        if (Thread.currentThread() == selectorThread) {
            key.interestOps(ops);
        } else {
            selectorTasks.add(() -> {
                key.interestOps(ops);
            });
            selector.wakeup();
        }
    }


    private void handleAccept(ServerSocketChannel serverChan, Selector selector) throws IOException {
        BidiMessagingProtocol temp = protocolFactory.get();

        SocketChannel clientChan = serverChan.accept(); //New socket channel for client
        clientChan.configureBlocking(false);
        final NonBlockingConnectionHandler handler = new NonBlockingConnectionHandler(
                readerFactory.get(),
                temp,
                clientChan,
                this);
        COUNTER_OF_ID ++;
        CONNECTIONS.addConnectionHnadler(COUNTER_OF_ID , handler);
        temp.start(COUNTER_OF_ID,CONNECTIONS);
        clientChan.register(selector, SelectionKey.OP_READ, handler);
    }

    private void handleReadWrite(SelectionKey key) {
        NonBlockingConnectionHandler handler = (NonBlockingConnectionHandler) key.attachment();
        if (key.isReadable()) {
            // continueRead() Return a Runnable function to read from the channel.
            // It will be sent to the Threadpool and there will be executed by the some thread.
            Runnable task = handler.continueRead();
            if (task != null) {
                pool.submit(handler, task);
            }
        }
	    if (key.isValid() && key.isWritable()) {
            handler.continueWrite();
        }
    }

    private void runSelectionThreadTasks() {
        while (!selectorTasks.isEmpty()) {
            selectorTasks.remove().run();
        }
    }

    @Override
    public void close() throws IOException {
        selector.close();
    }

}

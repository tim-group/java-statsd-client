package com.timgroup.statsd;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public final class NonBlockingUdpSender {
    private final Charset encoding;
    private final DatagramChannel clientSocket;
    private final ExecutorService executor;
    private final StatsDClientErrorHandler handler;

    public NonBlockingUdpSender(String hostname, int port, Charset encoding, StatsDClientErrorHandler handler) throws IOException {
        this.encoding = encoding;
        this.handler = handler;
        this.clientSocket = DatagramChannel.open();
        this.clientSocket.connect(new InetSocketAddress(hostname, port));

        this.executor = createExecutorService("StatsD-");
    }

    public NonBlockingUdpSender(String hostname, String threadPreffix, int port, Charset encoding, StatsDClientErrorHandler handler) throws IOException {
        this.encoding = encoding;
        this.handler = handler;
        this.clientSocket = DatagramChannel.open();
        this.clientSocket.connect(new InetSocketAddress(hostname, port));

        this.executor = createExecutorService(threadPreffix);
    }

    private static ExecutorService createExecutorService(String threadPreffix) {
        return Executors.newSingleThreadExecutor(new ThreadFactory() {
            final ThreadFactory delegate = Executors.defaultThreadFactory();

            @Override
            public Thread newThread(Runnable r) {
                Thread result = delegate.newThread(r);
                result.setName(threadPreffix + result.getName());
                result.setDaemon(true);
                return result;
            }
        });
    }


    public void stop() {
        try {
            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            handler.handle(e);
        } finally {
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (Exception e) {
                    handler.handle(e);
                }
            }
        }
    }

    public void send(final String message) {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    blockingSend(message);
                }
            });
        } catch (Exception e) {
            handler.handle(e);
        }
    }

    private void blockingSend(String message) {
        try {
            final byte[] sendData = message.getBytes(encoding);
            clientSocket.write(ByteBuffer.wrap(sendData));
        } catch (Exception e) {
            handler.handle(e);
        }
    }
}
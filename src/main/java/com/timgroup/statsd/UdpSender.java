package com.timgroup.statsd;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;

public class UdpSender {
    protected final Charset encoding;
    private final DatagramChannel clientSocket;
    private StatsDClientErrorHandler handler;

    public UdpSender(String hostname, int port, Charset encoding, StatsDClientErrorHandler handler) throws IOException {
        this.encoding = encoding;
        this.handler = handler;
        this.clientSocket = DatagramChannel.open();
        this.clientSocket.connect(new InetSocketAddress(hostname, port));
    }

    public void stop() {
        if (clientSocket != null) {
            try {
                clientSocket.close();
            } catch (Exception e) {
                handleException(e);
            }
        }
    }

    public void send(final String message) {
        try {
            final byte[] sendData = message.getBytes(encoding);
            blockingSend(ByteBuffer.wrap(sendData));
        } catch (Exception e) {
            handleException(e);
        }
    }

    protected int blockingSend(ByteBuffer data) {
        try {
            return clientSocket.write(data);
        } catch (Exception e) {
            handleException(e);
        }
        return -1;
    }

    protected void handleException(Exception e) {
        handler.handle(e);
    }
}
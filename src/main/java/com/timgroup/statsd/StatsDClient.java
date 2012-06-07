package com.timgroup.statsd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public final class StatsDClient {

    private final String prefix;
    private final DatagramSocket clientSocket;

    public StatsDClient(String prefix, String hostname, int port) {
        this.prefix = prefix;
        
        try {
            this.clientSocket = new DatagramSocket();
            this.clientSocket.connect(new InetSocketAddress(hostname, port));
        } catch (SocketException e) {
            throw new IllegalStateException(e);
        }
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
        final ThreadFactory delegate = Executors.defaultThreadFactory();
        @Override public Thread newThread(Runnable r) {
            Thread result = delegate.newThread(r);
            result.setName("StatsD-" + result.getName());
            return result;
        }
    });


    private void send(final String message) {
        executor.execute(new Runnable() {
            @Override public void run() {
                blockingSend(message);
            }
        });
    }

    public void stop() {
        try {
            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);
        }
        catch (Exception e) { }
        finally {
            if (clientSocket != null) {
                clientSocket.close();
            }
        }
    }

    private void blockingSend(String message) {
        try {
            final byte[] sendData = message.getBytes();
            final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length);
            clientSocket.send(sendPacket);
        } catch (Exception e) {
        }
    }

    public void incrementCounter(String aspect) {
        send(String.format("%s.%s:%d|c", prefix, aspect, 1));
    }

    public void recordGaugeValue(String aspect, int value) {
        send(String.format("%s.%s:%d|g", prefix, aspect, 1));
    }

    public void recordExecutionTime(String aspect, int timeInMs) {
        send(String.format("%s.%s:%d|ms", prefix, aspect, 1));
    }
}

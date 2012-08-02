package com.timgroup.statsd;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;

public class StatsDClientTest {

    private static final int STATSD_SERVER_PORT = 17254;
    private final StatsDClient client = new StatsDClient("my.prefix", "localhost", STATSD_SERVER_PORT);

    @After
    public void stop() throws Exception {
        client.stop();
    }

    @Test(timeout=5000L) public void
    sends_counter_increment_to_statsd() throws Exception {
        final DummyStatsDServer server = new DummyStatsDServer(STATSD_SERVER_PORT);
        
        client.incrementCounter("blah");
        server.waitForMessage();
        
        assertThat(server.messagesReceived(), contains("my.prefix.blah:1|c"));
    }

    private static final class DummyStatsDServer {
        private final List<String> messagesReceived = new ArrayList<String>();
        private final DatagramSocket server;

        public DummyStatsDServer(int port) throws SocketException {
            server = new DatagramSocket(port);
            new Thread(new Runnable() {
                @Override public void run() {
                    try {
                        final DatagramPacket packet = new DatagramPacket(new byte[256], 256);
                        server.receive(packet);
                        messagesReceived.add(new String(packet.getData()).trim());
                        server.close();
                    } catch (Exception e) { }
                }
            }).start();
        }
        
        public void waitForMessage() {
            while (messagesReceived.isEmpty()) {
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException e) {}}
        }
        
        public List<String> messagesReceived() {
            return new ArrayList<String>(messagesReceived);
        }
    }
}
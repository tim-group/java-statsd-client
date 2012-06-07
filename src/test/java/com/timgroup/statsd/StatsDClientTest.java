package com.timgroup.statsd;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
    sends_records_to_statsd() throws Exception {
        final List<String> messagesReceived = new ArrayList<String>();
        final DatagramSocket server = new DatagramSocket(STATSD_SERVER_PORT);
        
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
        
        client.incrementCounter("blah");
        while (messagesReceived.isEmpty()) { Thread.sleep(50L); }
        
        assertThat(messagesReceived, contains("my.prefix.blah:1|c"));
    }
}
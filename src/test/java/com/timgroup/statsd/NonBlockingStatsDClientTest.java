package com.timgroup.statsd;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.startsWith;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;

public final class NonBlockingStatsDClientTest {

    private static final int STATSD_SERVER_PORT = 17254;

    private final NonBlockingStatsDClient client = new NonBlockingStatsDClient("my.prefix", "localhost", STATSD_SERVER_PORT);
    private final DummyStatsDServer server = new DummyStatsDServer(STATSD_SERVER_PORT);

    @After
    public void stop() throws Exception {
        client.stop();
        server.stop();
    }

    @Test(timeout=5000L) public void
    sends_counter_value_to_statsd() throws Exception {
        client.count("mycount", 24);
        server.waitForMessage();
        
        assertThat(server.messagesReceived(), contains("my.prefix.mycount:24|c"));
    }

    @Test(timeout=5000L) public void
    sends_counter_increment_to_statsd() throws Exception {
        client.incrementCounter("myinc");
        server.waitForMessage();
        
        assertThat(server.messagesReceived(), contains("my.prefix.myinc:1|c"));
    }

    @Test(timeout=5000L) public void
    sends_counter_decrement_to_statsd() throws Exception {
        client.decrementCounter("mydec");
        server.waitForMessage();
        
        assertThat(server.messagesReceived(), contains("my.prefix.mydec:-1|c"));
    }

    @Test(timeout=5000L) public void
    sends_gauge_to_statsd() throws Exception {
        client.recordGaugeValue("mygauge", 423);
        server.waitForMessage();
        
        assertThat(server.messagesReceived(), contains("my.prefix.mygauge:423|g"));
    }

    @Test(timeout=5000L) public void
    sends_negagive_gauge_to_statsd_by_resetting_to_zero_first() throws Exception {
        client.recordGaugeValue("mygauge", -423);
        server.waitForMessage();

        assertThat(server.messagesReceived(), contains("my.prefix.mygauge:0|g\nmy.prefix.mygauge:-423|g"));
    }

    @Test(timeout=5000L) public void
    sends_gauge_positive_delta_to_statsd() throws Exception {
        client.recordGaugeDelta("mygauge", 423);
        server.waitForMessage();

        assertThat(server.messagesReceived(), contains("my.prefix.mygauge:+423|g"));
    }

    @Test(timeout=5000L) public void
    sends_gauge_negative_delta_to_statsd() throws Exception {
        client.recordGaugeDelta("mygauge", -423);
        server.waitForMessage();

        assertThat(server.messagesReceived(), contains("my.prefix.mygauge:-423|g"));
    }

    @Test(timeout=5000L) public void
    sends_gauge_zero_delta_to_statsd() throws Exception {
        client.recordGaugeDelta("mygauge", 0);
        server.waitForMessage();

        assertThat(server.messagesReceived(), contains("my.prefix.mygauge:+0|g"));
    }

    @Test(timeout=5000L) public void
    sends_set_to_statsd() throws Exception {
        client.recordSetEvent("myset", "test");
        server.waitForMessage();
        
        assertThat(server.messagesReceived(), contains("my.prefix.myset:test|s"));
    }

    @Test(timeout=5000L) public void
    sends_timer_to_statsd() throws Exception {
        client.recordExecutionTime("mytime", 123);
        server.waitForMessage();
        
        assertThat(server.messagesReceived(), contains("my.prefix.mytime:123|ms"));
    }

    @Test(timeout=5000L) public void
    allows_empty_prefix() {
        final NonBlockingStatsDClient emptyPrefixClient = new NonBlockingStatsDClient(" ", "localhost", STATSD_SERVER_PORT);
        try {
            emptyPrefixClient.count("mycount", 24);
            server.waitForMessage();
        } finally {
            emptyPrefixClient.stop();
        }
        assertThat(server.messagesReceived(), contains(startsWith("mycount:")));
    }

    @Test(timeout=5000L) public void
    allows_null_prefix() {
        final NonBlockingStatsDClient nullPrefixClient = new NonBlockingStatsDClient(null, "localhost", STATSD_SERVER_PORT);
        try {
            nullPrefixClient.count("mycount", 24);
            server.waitForMessage();
        } finally {
            nullPrefixClient.stop();
        }
        assertThat(server.messagesReceived(), contains(startsWith("mycount:")));
    }

    private static final class DummyStatsDServer {
        private final List<String> messagesReceived = new ArrayList<String>();
        private final DatagramSocket server;

        public DummyStatsDServer(int port) {
            try {
                server = new DatagramSocket(port);
            } catch (SocketException e) {
                throw new IllegalStateException(e);
            }
            new Thread(new Runnable() {
                @Override public void run() {
                    try {
                        final DatagramPacket packet = new DatagramPacket(new byte[256], 256);
                        server.receive(packet);
                        messagesReceived.add(new String(packet.getData()).trim());
                    } catch (Exception e) { }
                }
            }).start();
        }

        public void stop() {
            server.close();
        }

        public void waitForMessage() {
            while (messagesReceived.isEmpty()) {
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException e) {}
            }
        }

        public List<String> messagesReceived() {
            return new ArrayList<String>(messagesReceived);
        }
    }
}
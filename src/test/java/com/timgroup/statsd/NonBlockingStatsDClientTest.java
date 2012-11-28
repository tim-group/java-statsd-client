package com.timgroup.statsd;

import org.junit.After;
import org.junit.Test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class NonBlockingStatsDClientTest {

    private static final int STATSD_SERVER_PORT = 17254;
    private final NonBlockingStatsDClient client = new NonBlockingStatsDClient("my.prefix", "localhost", STATSD_SERVER_PORT);

    @After
    public void stop() throws Exception {
        client.stop();
    }

    @Test(timeout=5000L) public void
    sends_counter_value_to_statsd() throws Exception {
        final DummyStatsDServer server = new DummyStatsDServer(STATSD_SERVER_PORT);
        
        client.count("mycount", 24);
        server.waitForMessage();
        
        assertThat(server.messagesReceived(), contains("my.prefix.mycount:24|c"));
    }

    @Test(timeout=5000L) public void
    sends_counter_value_to_statsd_with_null_tags() throws Exception {
        final DummyStatsDServer server = new DummyStatsDServer(STATSD_SERVER_PORT);
        
        client.count("mycount", 24, null);
        server.waitForMessage();
        
        assertThat(server.messagesReceived(), contains("my.prefix.mycount:24|c"));
    }

    @Test(timeout=5000L) public void
    sends_counter_value_to_statsd_with_empty_tags() throws Exception {
        final DummyStatsDServer server = new DummyStatsDServer(STATSD_SERVER_PORT);
        
        client.count("mycount", 24, new String[]{});
        server.waitForMessage();
        
        assertThat(server.messagesReceived(), contains("my.prefix.mycount:24|c"));
    }

    @Test(timeout=5000L) public void
    sends_counter_value_to_statsd_with_tags() throws Exception {
        final DummyStatsDServer server = new DummyStatsDServer(STATSD_SERVER_PORT);
        
        client.count("mycount", 24, new String[]{"foo:bar","baz"});
        server.waitForMessage();
        
        assertThat(server.messagesReceived(), contains("my.prefix.mycount:24|c|#baz,foo:bar"));
    }

    @Test(timeout=5000L) public void
    sends_counter_increment_to_statsd() throws Exception {
        final DummyStatsDServer server = new DummyStatsDServer(STATSD_SERVER_PORT);
        
        client.incrementCounter("myinc");
        server.waitForMessage();
        
        assertThat(server.messagesReceived(), contains("my.prefix.myinc:1|c"));
    }

    @Test(timeout=5000L) public void
    sends_counter_increment_to_statsd_with_tags() throws Exception {
        final DummyStatsDServer server = new DummyStatsDServer(STATSD_SERVER_PORT);
        
        client.incrementCounter("myinc", new String[]{"foo:bar","baz"});
        server.waitForMessage();
        
        assertThat(server.messagesReceived(), contains("my.prefix.myinc:1|c|#baz,foo:bar"));
    }

    @Test(timeout=5000L) public void
    sends_counter_decrement_to_statsd() throws Exception {
        final DummyStatsDServer server = new DummyStatsDServer(STATSD_SERVER_PORT);
        
        client.decrementCounter("mydec");
        server.waitForMessage();
        
        assertThat(server.messagesReceived(), contains("my.prefix.mydec:-1|c"));
    }

    @Test(timeout=5000L) public void
    sends_counter_decrement_to_statsd_with_tags() throws Exception {
        final DummyStatsDServer server = new DummyStatsDServer(STATSD_SERVER_PORT);
        
        client.decrementCounter("mydec", new String[]{"foo:bar", "baz"});
        server.waitForMessage();
        
        assertThat(server.messagesReceived(), contains("my.prefix.mydec:-1|c|#baz,foo:bar"));
    }

    @Test(timeout=5000L) public void
    sends_gauge_to_statsd() throws Exception {
        final DummyStatsDServer server = new DummyStatsDServer(STATSD_SERVER_PORT);
        
        client.recordGaugeValue("mygauge", 423);
        server.waitForMessage();
        
        assertThat(server.messagesReceived(), contains("my.prefix.mygauge:423|g"));
    }

    @Test(timeout=5000L) public void
    sends_double_gauge_to_statsd() throws Exception {
        final DummyStatsDServer server = new DummyStatsDServer(STATSD_SERVER_PORT);
        
        client.recordGaugeValue("mygauge", 0.423);
        server.waitForMessage();
        
        assertThat(server.messagesReceived(), contains("my.prefix.mygauge:0.423|g"));
    }

    @Test(timeout=5000L) public void
    sends_gauge_to_statsd_with_tags() throws Exception {
        final DummyStatsDServer server = new DummyStatsDServer(STATSD_SERVER_PORT);
        
        client.recordGaugeValue("mygauge", 423, new String[]{"foo:bar","baz"});
        server.waitForMessage();
        
        assertThat(server.messagesReceived(), contains("my.prefix.mygauge:423|g|#baz,foo:bar"));
    }

    @Test(timeout=5000L) public void
    sends_double_gauge_to_statsd_with_tags() throws Exception {
        final DummyStatsDServer server = new DummyStatsDServer(STATSD_SERVER_PORT);
        
        client.recordGaugeValue("mygauge", 0.423, new String[]{"foo:bar","baz"});
        server.waitForMessage();
        
        assertThat(server.messagesReceived(), contains("my.prefix.mygauge:0.423|g|#baz,foo:bar"));
    }

    @Test(timeout=5000L) public void
    sends_histogram_to_statsd() throws Exception {
        final DummyStatsDServer server = new DummyStatsDServer(STATSD_SERVER_PORT);

        client.recordHistogramValue("myhistogram", 423);
        server.waitForMessage();

        assertThat(server.messagesReceived(), contains("my.prefix.myhistogram:423|h"));
    }

    @Test(timeout=5000L) public void
    sends_double_histogram_to_statsd() throws Exception {
        final DummyStatsDServer server = new DummyStatsDServer(STATSD_SERVER_PORT);

        client.recordHistogramValue("myhistogram", 0.423);
        server.waitForMessage();

        assertThat(server.messagesReceived(), contains("my.prefix.myhistogram:0.423|h"));
    }

    @Test(timeout=5000L) public void
    sends_histogram_to_statsd_with_tags() throws Exception {
        final DummyStatsDServer server = new DummyStatsDServer(STATSD_SERVER_PORT);

        client.recordHistogramValue("myhistogram", 423, new String[]{"foo:bar","baz"});
        server.waitForMessage();

        assertThat(server.messagesReceived(), contains("my.prefix.myhistogram:423|h|#baz,foo:bar"));
    }

    @Test(timeout=5000L) public void
    sends_double_histogram_to_statsd_with_tags() throws Exception {
        final DummyStatsDServer server = new DummyStatsDServer(STATSD_SERVER_PORT);

        client.recordHistogramValue("myhistogram", 0.423, new String[]{"foo:bar","baz"});
        server.waitForMessage();

        assertThat(server.messagesReceived(), contains("my.prefix.myhistogram:0.423|h|#baz,foo:bar"));
    }

    @Test(timeout=5000L) public void
    sends_timer_to_statsd() throws Exception {
        final DummyStatsDServer server = new DummyStatsDServer(STATSD_SERVER_PORT);
        
        client.recordExecutionTime("mytime", 123);
        server.waitForMessage();
        
        assertThat(server.messagesReceived(), contains("my.prefix.mytime:123|ms"));
    }

    @Test(timeout=5000L) public void
    sends_timer_to_statsd_with_tags() throws Exception {
        final DummyStatsDServer server = new DummyStatsDServer(STATSD_SERVER_PORT);
        
        client.recordExecutionTime("mytime", 123, new String[]{"foo:bar","baz"});
        server.waitForMessage();
        
        assertThat(server.messagesReceived(), contains("my.prefix.mytime:123|ms|#baz,foo:bar"));
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

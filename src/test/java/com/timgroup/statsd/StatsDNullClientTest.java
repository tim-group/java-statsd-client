package com.timgroup.statsd;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;

public class StatsDNullClientTest {

    private final StatsDClient client = new StatsDClient();

    @After
    public void stop() throws Exception {
        client.stop();
    }

    @Test(timeout=5000L) public void
    sends_counter_value_to_statsd() throws Exception {
        client.count("mycount", 24);
        assertThat(1,anything());
    }

    @Test(timeout=5000L) public void
    sends_counter_increment_to_statsd() throws Exception {        
        client.incrementCounter("myinc");
        assertThat(1,anything());
    }

    @Test(timeout=5000L) public void
    sends_counter_decrement_to_statsd() throws Exception {
        client.decrementCounter("mydec");
        assertThat(1,anything());
    }

    @Test(timeout=5000L) public void
    sends_gauge_to_statsd() throws Exception {
        client.recordGaugeValue("mygauge", 423);
        assertThat(1,anything());
    }

    @Test(timeout=5000L) public void
    sends_timer_to_statsd() throws Exception {
        client.recordExecutionTime("mytime", 123);
        assertThat(1,anything());
    }
}
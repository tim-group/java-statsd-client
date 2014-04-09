package com.timgroup.statsd;


import java.net.SocketException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class NonBlockingStatsDClientPerfTest {


    private static final int STATSD_SERVER_PORT = 17255;
    private static final Random RAND = new Random();
    private final NonBlockingStatsDClient client = new NonBlockingStatsDClient("my.prefix", "localhost", STATSD_SERVER_PORT);
    private final ExecutorService executor = Executors.newFixedThreadPool(20);
    private DummyStatsDServer server;

    @Before
    public void start() throws SocketException {
        server = new DummyStatsDServer(STATSD_SERVER_PORT);
    }

    @After
    public void stop() throws Exception {
        client.stop();
        server.close();
    }

    @Test(timeout=30000)
    public void perf_test() throws Exception {

        int testSize = 10000;
        for(int i = 0; i < testSize; ++i) {
            executor.submit(new Runnable() {
                public void run() {
                    client.count("mycount", RAND.nextInt());
                }
            });

        }

        executor.shutdown();
        executor.awaitTermination(20, TimeUnit.SECONDS);

        for(int i = 0; i < 20000 && server.messagesReceived().size() < testSize; i += 50) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {}
        }

        assertEquals(testSize, server.messagesReceived().size());
    }
}

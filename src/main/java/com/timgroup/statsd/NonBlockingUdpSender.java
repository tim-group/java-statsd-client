package com.timgroup.statsd;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public final class NonBlockingUdpSender extends UdpSender {
    private final ExecutorService executor;

    public NonBlockingUdpSender(String hostname, int port, Charset encoding, StatsDClientErrorHandler handler) throws IOException {
        super(hostname, port, encoding, handler);
        this.executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
            final ThreadFactory delegate = Executors.defaultThreadFactory();

            @Override
            public Thread newThread(Runnable r) {
                Thread result = delegate.newThread(r);
                result.setName("StatsD-" + result.getName());
                result.setDaemon(true);
                return result;
            }
        });
    }

    @Override
    public void stop() {
        try {
            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            handleException(e);
        } finally {
            super.stop();
        }
    }

    @Override
    public void send(final String message) {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    blockingSend(message);
                }
            });
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void blockingSend(String message) {
        super.send(message);
    }
}
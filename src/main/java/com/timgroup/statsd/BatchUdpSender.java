package com.timgroup.statsd;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.*;

public class BatchUdpSender extends UdpSender {
    private final ExecutorService executor;
    private static final int PACKET_SIZE_BYTES = 1500;
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<String>();

    public BatchUdpSender(String hostname, int port, Charset encoding, StatsDClientErrorHandler handler) throws IOException {
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
        this.executor.submit(new QueueConsumer());
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
        queue.offer(message);
    }

    private class QueueConsumer implements Runnable {
        private final ByteBuffer buffer = ByteBuffer.allocateDirect(PACKET_SIZE_BYTES);

        @Override
        public void run() {
            while (!executor.isShutdown()) {
                try {
                    final String message = queue.poll(100, TimeUnit.MILLISECONDS);
                    if (null != message) {
                        final byte[] data = message.getBytes(encoding);
                        if (buffer.remaining() < (data.length + 1)) {
                            send();
                        }
                        if (buffer.position() > 0) {
                            buffer.put((byte) '\n');
                        }
                        buffer.put(data);
                        if (buffer.position() > PACKET_SIZE_BYTES / 2 && null == queue.peek()) {
                            send();
                        }
                    } else {
                        send();
                    }
                } catch (Exception e) {
                    handleException(e);
                }
            }
        }

        private void send() {
            int sizeOfBuffer = buffer.position();
            buffer.flip();
            int sentBytes = blockingSend(buffer);
            buffer.limit(buffer.capacity());
            buffer.rewind();
            if (sizeOfBuffer != sentBytes) {
                handleException(new IOException(
                        String.format("Could not send entirely stat %s. Only sent %d bytes out of %d bytes",
                                buffer.toString(),
                                sentBytes,
                                sizeOfBuffer)));
            }
        }
    }
}
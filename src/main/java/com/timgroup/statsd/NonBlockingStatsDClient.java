package com.timgroup.statsd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * A simple StatsD client implementation facilitating metrics recording.
 * 
 * <p>Upon instantiation, this client will establish a socket connection to a StatsD instance
 * running on the specified host and port. Metrics are then sent over this connection as they are
 * received by the client.
 * </p>
 * 
 * <p>Three key methods are provided for the submission of data-points for the application under
 * scrutiny:
 * <ul>
 *   <li>{@link #incrementCounter} - adds one to the value of the specified named counter</li>
 *   <li>{@link #recordGaugeValue} - records the latest fixed value for the specified named gauge</li>
 *   <li>{@link #recordExecutionTime} - records an execution time in milliseconds for the specified named operation</li>
 *   <li>{@link #recordHistogramValue} - records a value, to be tracked with average, maximum, and percentiles</li>
 * </ul>
 * From the perspective of the application, these methods are non-blocking, with the resulting
 * IO operations being carried out in a separate thread. Furthermore, these methods are guaranteed
 * not to throw an exception which may disrupt application execution.
 * </p>
 * 
 * <p>As part of a clean system shutdown, the {@link #stop()} method should be invoked
 * on any StatsD clients.</p>
 * 
 * @author Tom Denley
 *
 */
public final class NonBlockingStatsDClient implements StatsDClient {

    private static final StatsDClientErrorHandler NO_OP_HANDLER = new StatsDClientErrorHandler() {
        @Override public void handle(Exception e) { /* No-op */ }
    };

    private final String prefix;
    private final DatagramSocket clientSocket;
    private final StatsDClientErrorHandler handler;

    private final ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
        final ThreadFactory delegate = Executors.defaultThreadFactory();
        @Override public Thread newThread(Runnable r) {
            Thread result = delegate.newThread(r);
            result.setName("StatsD-" + result.getName());
            return result;
        }
    });

    /**
     * Create a new StatsD client communicating with a StatsD instance on the
     * specified host and port. All messages send via this client will have
     * their keys prefixed with the specified string. The new client will
     * attempt to open a connection to the StatsD server immediately upon
     * instantiation, and may throw an exception if that a connection cannot
     * be established. Once a client has been instantiated in this way, all
     * exceptions thrown during subsequent usage are consumed, guaranteeing
     * that failures in metrics will not affect normal code execution.
     * 
     * @param prefix
     *     the prefix to apply to keys sent via this client
     * @param hostname
     *     the host name of the targeted StatsD server
     * @param port
     *     the port of the targeted StatsD server
     * @throws StatsDClientException
     *     if the client could not be started
     */
    public NonBlockingStatsDClient(String prefix, String hostname, int port) throws StatsDClientException {
        this(prefix, hostname, port, NO_OP_HANDLER);
    }
    
    /**
     * Create a new StatsD client communicating with a StatsD instance on the
     * specified host and port. All messages send via this client will have
     * their keys prefixed with the specified string. The new client will
     * attempt to open a connection to the StatsD server immediately upon
     * instantiation, and may throw an exception if that a connection cannot
     * be established. Once a client has been instantiated in this way, all
     * exceptions thrown during subsequent usage are passed to the specified
     * handler and then consumed, guaranteeing that failures in metrics will
     * not affect normal code execution.
     * 
     * @param prefix
     *     the prefix to apply to keys sent via this client
     * @param hostname
     *     the host name of the targeted StatsD server
     * @param port
     *     the port of the targeted StatsD server
     * @param errorHandler
     *     handler to use when an exception occurs during usage
     * @throws StatsDClientException
     *     if the client could not be started
     */
    public NonBlockingStatsDClient(String prefix, String hostname, int port, StatsDClientErrorHandler errorHandler) throws StatsDClientException {
        this.prefix = prefix;
        this.handler = errorHandler;
        
        try {
            this.clientSocket = new DatagramSocket();
            this.clientSocket.connect(new InetSocketAddress(hostname, port));
        } catch (Exception e) {
            throw new StatsDClientException("Failed to start StatsD client", e);
        }
    }

    /**
     * Cleanly shut down this StatsD client. This method may throw an exception if
     * the socket cannot be closed.
     */
    @Override
    public void stop() {
        try {
            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            handler.handle(e);
        }
        finally {
            if (clientSocket != null) {
                clientSocket.close();
            }
        }
    }

    /**
     * Generate a suffix conveying the given tag list to the client
     */
    String tagString(String[] tags) {
        if(tags == null || tags.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder("|#");
        for(int n=tags.length - 1; n>=0; n--) {
            sb.append(tags[n]);
            if(n > 0) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * Adjusts the specified counter by a given delta.
     * 
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     * 
     * @param aspect
     *     the name of the counter to adjust
     * @param delta
     *     the amount to adjust the counter by
     * @param tags
     *     array of tags to be added to the data
     */
    @Override
    public void count(String aspect, int delta, String[] tags) {
        send(String.format("%s.%s:%d|c%s", prefix, aspect, delta, tagString(tags)));
    }

    /**
     * Adjusts the specified counter by a given delta.
     * 
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     * 
     * @param aspect
     *     the name of the counter to adjust
     * @param delta
     *     the amount to adjust the counter by
     */
    @Override
    public void count(String aspect, int delta) {
        send(String.format("%s.%s:%d|c", prefix, aspect, delta));
    }

    /**
     * Increments the specified counter by one.
     * 
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     * 
     * @param aspect
     *     the name of the counter to increment
     * @param tags
     *     array of tags to be added to the data
     */
    @Override
    public void incrementCounter(String aspect, String[] tags) {
        count(aspect, 1, tags);
    }

    /**
     * Increments the specified counter by one.
     * 
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     * 
     * @param aspect
     *     the name of the counter to increment
     */
    @Override
    public void incrementCounter(String aspect) {
        count(aspect, 1);
    }

    /**
     * Convenience method equivalent to {@link #incrementCounter(String, String[])}. 
     */
    @Override
    public void increment(String aspect, String[] tags) {
        incrementCounter(aspect, tags);
    }

    /**
     * Convenience method equivalent to {@link #incrementCounter(String)}. 
     */
    @Override
    public void increment(String aspect) {
        incrementCounter(aspect);
    }

    /**
     * Decrements the specified counter by one.
     * 
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     * 
     * @param aspect
     *     the name of the counter to decrement
     * @param tags
     *     array of tags to be added to the data
     */
    @Override
    public void decrementCounter(String aspect, String[] tags) {
        count(aspect, -1, tags);
    }

    /**
     * Decrements the specified counter by one.
     * 
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     * 
     * @param aspect
     *     the name of the counter to decrement
     */
    @Override
    public void decrementCounter(String aspect) {
        count(aspect, -1);
    }

    /**
     * Convenience method equivalent to {@link #decrementCounter(String, String[])}. 
     */
    @Override
    public void decrement(String aspect, String[] tags) {
        decrementCounter(aspect, tags);
    }

    /**
     * Convenience method equivalent to {@link #decrementCounter(String)}. 
     */
    @Override
    public void decrement(String aspect) {
        decrementCounter(aspect);
    }

    /**
     * Records the latest fixed value for the specified named gauge.
     * 
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     * 
     * @param aspect
     *     the name of the gauge
     * @param value
     *     the new reading of the gauge
     * @param tags
     *     array of tags to be added to the data
     */
    @Override
    public void recordGaugeValue(String aspect, double value, String[] tags) {
        /* Intentionally using %s rather than %f here to avoid
         * padding with extra 0s to represent precision */
        send(String.format("%s.%s:%s|g%s", prefix, aspect, value, tagString(tags)));
    }

    /**
     * Records the latest fixed value for the specified named gauge.
     * 
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     * 
     * @param aspect
     *     the name of the gauge
     * @param value
     *     the new reading of the gauge
     */
    @Override
    public void recordGaugeValue(String aspect, double value) {
        /* Intentionally using %s rather than %f here to avoid
         * padding with extra 0s to represent precision */
        send(String.format("%s.%s:%s|g", prefix, aspect, value));
    }

    /**
     * Convenience method equivalent to {@link #recordGaugeValue(String, double, String[])}.
     */
    @Override
    public void gauge(String aspect, double value, String[] tags) {
        recordGaugeValue(aspect, value, tags);
    }

    /**
     * Convenience method equivalent to {@link #recordGaugeValue(String, double)}.
     */
    @Override
    public void gauge(String aspect, double value) {
        recordGaugeValue(aspect, value);
    }


    /**
     * Records the latest fixed value for the specified named gauge.
     * 
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     * 
     * @param aspect
     *     the name of the gauge
     * @param value
     *     the new reading of the gauge
     * @param tags
     *     array of tags to be added to the data
     */
    @Override
    public void recordGaugeValue(String aspect, int value, String[] tags) {
        send(String.format("%s.%s:%d|g%s", prefix, aspect, value, tagString(tags)));
    }

    /**
     * Records the latest fixed value for the specified named gauge.
     * 
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     * 
     * @param aspect
     *     the name of the gauge
     * @param value
     *     the new reading of the gauge
     */
    @Override
    public void recordGaugeValue(String aspect, int value) {
        send(String.format("%s.%s:%d|g", prefix, aspect, value));
    }

    /**
     * Convenience method equivalent to {@link #recordGaugeValue(String, int, String[])}. 
     */
    @Override
    public void gauge(String aspect, int value, String[] tags) {
        recordGaugeValue(aspect, value, tags);
    }

    /**
     * Convenience method equivalent to {@link #recordGaugeValue(String, int)}. 
     */
    @Override
    public void gauge(String aspect, int value) {
        recordGaugeValue(aspect, value);
    }

    /**
     * Records an execution time in milliseconds for the specified named operation.
     * 
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     * 
     * @param aspect
     *     the name of the timed operation
     * @param timeInMs
     *     the time in milliseconds
     * @param tags
     *     array of tags to be added to the data
     */
    @Override
    public void recordExecutionTime(String aspect, int timeInMs, String[] tags) {
        send(String.format("%s.%s:%d|ms%s", prefix, aspect, timeInMs, tagString(tags)));
    }

    /**
     * Records an execution time in milliseconds for the specified named operation.
     * 
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     * 
     * @param aspect
     *     the name of the timed operation
     * @param timeInMs
     *     the time in milliseconds
     */
    @Override
    public void recordExecutionTime(String aspect, int timeInMs) {
        send(String.format("%s.%s:%d|ms", prefix, aspect, timeInMs));
    }

    /**
     * Convenience method equivalent to {@link #recordExecutionTime(String, int, String[])}. 
     */
    @Override
    public void time(String aspect, int value, String[] tags) {
        recordExecutionTime(aspect, value, tags);
    }

    /**
     * Convenience method equivalent to {@link #recordExecutionTime(String, int)}. 
     */
    @Override
    public void time(String aspect, int value) {
        recordExecutionTime(aspect, value);
    }

    /**
     * Records a value for the specified named histogram.
     *
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     *
     * @param aspect
     *     the name of the histogram
     * @param value
     *     the value to be incorporated in the histogram
     * @param tags
     *     array of tags to be added to the data
     */
    @Override
    public void recordHistogramValue(String aspect, double value, String[] tags) {
        /* Intentionally using %s rather than %f here to avoid
         * padding with extra 0s to represent precision */
        send(String.format("%s.%s:%s|h%s", prefix, aspect, value, tagString(tags)));
    }

    /**
     * Records a value for the specified named histogram.
     *
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     *
     * @param aspect
     *     the name of the histogram
     * @param value
     *     the value to be incorporated in the histogram
     */
    @Override
    public void recordHistogramValue(String aspect, double value) {
        /* Intentionally using %s rather than %f here to avoid
         * padding with extra 0s to represent precision */
        send(String.format("%s.%s:%s|h", prefix, aspect, value));
    }

    /**
     * Convenience method equivalent to {@link #recordHistogramValue(String, double, String[])}.
     */
    @Override
    public void histogram(String aspect, double value, String[] tags) {
        recordHistogramValue(aspect, value, tags);
    }

    /**
     * Convenience method equivalent to {@link #recordHistogramValue(String, double, String[])}.
     */
    @Override
    public void histogram(String aspect, double value) {
        recordHistogramValue(aspect, value);
    }

    /**
     * Records a value for the specified named histogram.
     * 
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     * 
     * @param aspect
     *     the name of the histogram
     * @param value
     *     the value to be incorporated in the histogram
     * @param tags
     *     array of tags to be added to the data
     */
    @Override
    public void recordHistogramValue(String aspect, int value, String[] tags) {
        send(String.format("%s.%s:%d|h%s", prefix, aspect, value, tagString(tags)));
    }

    /**
     * Records a value for the specified named histogram.
     * 
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     * 
     * @param aspect
     *     the name of the histogram
     * @param value
     *     the value to be incorporated in the histogram
     */
    @Override
    public void recordHistogramValue(String aspect, int value) {
        send(String.format("%s.%s:%d|h", prefix, aspect, value));
    }

    /**
     * Convenience method equivalent to {@link #recordHistogramValue(String, int, String[])}. 
     */
    @Override
    public void histogram(String aspect, int value, String[] tags) {
        recordHistogramValue(aspect, value, tags);
    }

    /**
     * Convenience method equivalent to {@link #recordHistogramValue(String, int)}. 
     */
    @Override
    public void histogram(String aspect, int value) {
        recordHistogramValue(aspect, value);
    }

    private void send(final String message) {
        try {
            executor.execute(new Runnable() {
                @Override public void run() {
                    blockingSend(message);
                }
            });
        }
        catch (Exception e) {
            handler.handle(e);
        }
    }

    private void blockingSend(String message) {
        try {
            final byte[] sendData = message.getBytes();
            final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length);
            clientSocket.send(sendPacket);
        } catch (Exception e) {
            handler.handle(e);
        }
    }
}

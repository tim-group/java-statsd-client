package com.timgroup.statsd;

import java.nio.charset.Charset;

import static com.timgroup.statsd.MessageUtil.*;

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
public final class NonBlockingStatsDClient extends ConvenienceMethodProvidingStatsDClient {

    private static final Charset STATS_D_ENCODING = Charset.forName("UTF-8");

    private static final StatsDClientErrorHandler NO_OP_HANDLER = new StatsDClientErrorHandler() {
        @Override public void handle(Exception e) { /* No-op */ }
    };

    private final String prefix;
    private final NonBlockingUdpSender sender;

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
     *     the prefix to apply to keys sent via this client (can be null or empty for no prefix)
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
     *     the prefix to apply to keys sent via this client (can be null or empty for no prefix)
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
        this.prefix = (prefix == null || prefix.trim().isEmpty()) ? "" : (prefix.trim() + ".");

        try {
            this.sender = new NonBlockingUdpSender(hostname, port, STATS_D_ENCODING, errorHandler);
        } catch (Exception e) {
            throw new StatsDClientException("Failed to start StatsD client", e);
        }
    }

    String getPrefix() {
        return prefix;
    }

    /**
     * Cleanly shut down this StatsD client. This method may throw an exception if
     * the socket cannot be closed.
     */
    @Override
    public void stop() {
        sender.stop();
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
     * @param sampleRate
     *     the sampling rate being employed. For example, a rate of 0.1 would tell StatsD that this counter is being sent
     *     sampled every 1/10th of the time.
     */
    @Override
    public void count(String aspect, long delta, double sampleRate) {
        send(makeCountMessage(prefix, aspect, delta, sampleRate));
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
    public void recordGaugeValue(String aspect, long value) {
        recordGaugeCommon(aspect, Long.toString(value), value < 0, false);
    }

    @Override
    public void recordGaugeValue(String aspect, double value) {
        recordGaugeCommon(aspect, stringValueOf(value), value < 0, false);
    }

    @Override
    public void recordGaugeDelta(String aspect, long value) {
        recordGaugeCommon(aspect, Long.toString(value), value < 0, true);
    }

    @Override
    public void recordGaugeDelta(String aspect, double value) {
        recordGaugeCommon(aspect, stringValueOf(value), value < 0, true);
    }

    private void recordGaugeCommon(String aspect, String value, boolean negative, boolean delta) {
        send(makeGaugeMessage(prefix, aspect, value, negative, delta));
    }

    /**
     * StatsD supports counting unique occurrences of events between flushes, Call this method to records an occurrence
     * of the specified named event.
     *
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     *
     * @param aspect
     *     the name of the set
     * @param eventName
     *     the value to be added to the set
     */
    @Override
    public void recordSetEvent(String aspect, String eventName) {
        send(makeRecordSetEventMessage(prefix, aspect, eventName));
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
    public void recordExecutionTime(String aspect, long timeInMs, double sampleRate) {
        send(makeRecordExecutionTimeMessage(prefix, aspect, timeInMs, sampleRate));
    }

    public Pipeline pipeline() {
        return new NonBlockingPipeline(this);
    }

    void send(final String message) {
        sender.send(message);
    }

}

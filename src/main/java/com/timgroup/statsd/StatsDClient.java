package com.timgroup.statsd;

/**
 * Describes a client connection to a StatsD server, which may be used to post metrics
 * in the form of counters, timers, and gauges.
 *
 * <p>Three key methods are provided for the submission of data-points for the application under
 * scrutiny:
 * <ul>
 *   <li>{@link #incrementCounter} - adds one to the value of the specified named counter</li>
 *   <li>{@link #recordGaugeValue} - records the latest fixed value for the specified named gauge</li>
 *   <li>{@link #recordExecutionTime} - records an execution time in milliseconds for the specified named operation</li>
 * </ul>
 *
 * @author Tom Denley
 *
 */
public interface StatsDClient {

    /**
     * Cleanly shut down this StatsD client. This method may throw an exception if
     * the socket cannot be closed.
     */
    void stop();

    /**
     * Adjusts the specified counter by a given delta.
     *
     * <p>This method is a DataDog extension, and may not work with other servers.</p>
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
    void count(String aspect, int delta, String... tags);

    /**
     * Increments the specified counter by one.
     *
     * <p>This method is a DataDog extension, and may not work with other servers.</p>
     *
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     *
     * @param aspect
     *     the name of the counter to increment
     * @param tags
     *     array of tags to be added to the data
     */
    void incrementCounter(String aspect, String... tags);

    /**
     * Convenience method equivalent to {@link #incrementCounter(String, String[])}.
     */
    void increment(String aspect, String... tags);

    /**
     * Decrements the specified counter by one.
     *
     * <p>This method is a DataDog extension, and may not work with other servers.</p>
     *
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     *
     * @param aspect
     *     the name of the counter to decrement
     * @param tags
     *     array of tags to be added to the data
     */
    void decrementCounter(String aspect, String... tags);

    /**
     * Convenience method equivalent to {@link #decrementCounter(String, String[])}.
     */
    void decrement(String aspect, String... tags);

    /**
     * Records the latest fixed value for the specified named gauge.
     *
     * <p>This method is a DataDog extension, and may not work with other servers.</p>
     *
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     *
     * @param aspect
     *     the name of the gauge
     * @param value
     *     the new reading of the gauge
     */
    void recordGaugeValue(String aspect, double value, String... tags);

    /**
     * Convenience method equivalent to {@link #recordGaugeValue(String, double, String[])}.
     */
    void gauge(String aspect, double value, String... tags);

    /**
     * Records the latest fixed value for the specified named gauge.
     *
     * <p>This method is a DataDog extension, and may not work with other servers.</p>
     *
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     *
     * @param aspect
     *     the name of the gauge
     * @param value
     *     the new reading of the gauge
     */
    void recordGaugeValue(String aspect, int value, String... tags);

    /**
     * Convenience method equivalent to {@link #recordGaugeValue(String, int, String[])}.
     */
    void gauge(String aspect, int value, String... tags);

    /**
     * Records an execution time in milliseconds for the specified named operation.
     *
     * <p>This method is a DataDog extension, and may not work with other servers.</p>
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
    void recordExecutionTime(String aspect, long timeInMs, String... tags);

    /**
     * Convenience method equivalent to {@link #recordExecutionTime(String, long, String[])}.
     */
    void time(String aspect, long value, String... tags);

    /**
     * Records a value for the specified named histogram.
     *
     * <p>This method is a DataDog extension, and may not work with other servers.</p>
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
    void recordHistogramValue(String aspect, double value, String... tags);

    /**
     * Convenience method equivalent to {@link #recordHistogramValue(String, double, String[])}.
     */
    void histogram(String aspect, double value, String... tags);

    /**
     * Records a value for the specified named histogram.
     *
     * <p>This method is a DataDog extension, and may not work with other servers.</p>
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
    void recordHistogramValue(String aspect, int value, String... tags);

    /**
     * Convenience method equivalent to {@link #recordHistogramValue(String, int, String[])}.
     */
    void histogram(String aspect, int value, String... tags);

}

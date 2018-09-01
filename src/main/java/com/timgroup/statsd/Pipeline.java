package com.timgroup.statsd;

/**
 * Pipeline supports same functionality with StatsDClient but with different return type of Pipeline but void.
 *
 * Created by c6s on 18-9-1
 */
public interface Pipeline {
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
    Pipeline count(String aspect, long delta);

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
    Pipeline count(String aspect, long delta, double sampleRate);

    /**
     * Increments the specified counter by one.
     *
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     *
     * @param aspect
     *     the name of the counter to increment
     */
    Pipeline incrementCounter(String aspect);

    /**
     * Convenience method equivalent to {@link #incrementCounter(String)}.
     */
    Pipeline increment(String aspect);

    /**
     * Decrements the specified counter by one.
     *
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     *
     * @param aspect
     *     the name of the counter to decrement
     */
    Pipeline decrementCounter(String aspect);

    /**
     * Convenience method equivalent to {@link #decrementCounter(String)}.
     */
    Pipeline decrement(String aspect);

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
    Pipeline recordGaugeValue(String aspect, long value);

    /**
     * Convenience method equivalent to {@link #recordGaugeValue(String, long)} but for double values.
     */
    Pipeline recordGaugeValue(String aspect, double value);

    /**
     * Records a change in the value of the specified named gauge.
     *
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     *
     * @param aspect
     *     the name of the gauge
     * @param delta
     *     the +/- delta to apply to the gauge
     */
    Pipeline recordGaugeDelta(String aspect, long delta);

    /**
     * Convenience method equivalent to {@link #recordGaugeDelta(String, long)} but for double deltas.
     */
    Pipeline recordGaugeDelta(String aspect, double delta);

    /**
     * Convenience method equivalent to {@link #recordGaugeValue(String, long)}.
     */
    Pipeline gauge(String aspect, long value);

    /**
     * Convenience method equivalent to {@link #recordGaugeValue(String, double)}.
     */
    Pipeline gauge(String aspect, double value);

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
    Pipeline recordSetEvent(String aspect, String eventName);

    /**
     * Convenience method equivalent to {@link #recordSetEvent(String, String)}.
     */
    Pipeline set(String aspect, String eventName);

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
    Pipeline recordExecutionTime(String aspect, long timeInMs);

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
     *     the sampling rate being employed. For example, a rate of 0.1 would tell StatsD that this timer is being sent
     *     sampled every 1/10th of the time, so that it updates its timer_counters appropriately.
     */
    Pipeline recordExecutionTime(String aspect, long timeInMs, double sampleRate);

    /**
     * Records an execution time in milliseconds for the specified named operation. The execution
     * time is calculated as the delta between the specified start time and the current system
     * time (using {@link System#currentTimeMillis()})
     *
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     *
     * @param aspect
     *     the name of the timed operation
     * @param timeInMs
     *     the system time, in millis, at the start of the operation that has just completed
     */
    Pipeline recordExecutionTimeToNow(String aspect, long systemTimeMillisAtStart);

    /**
     * Convenience method equivalent to {@link #recordExecutionTime(String, long)}.
     */
    Pipeline time(String aspect, long value);

    /**
     * Flush buffered metrics.
     * A pipeline will not preform any more actions if flushed.
     */
    void flush();
}

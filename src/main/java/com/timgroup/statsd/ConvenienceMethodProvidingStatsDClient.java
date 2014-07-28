package com.timgroup.statsd;

public abstract class ConvenienceMethodProvidingStatsDClient implements StatsDClient {

    public ConvenienceMethodProvidingStatsDClient() {
        super();
    }

    @Override
    public final void count(String aspect, long delta) {
        count(aspect, delta, 1.0);
    }

    /**
     * Convenience method equivalent to {@link #count(String, long)} with a value of 1.
     */
    @Override
    public final void incrementCounter(String aspect) {
        count(aspect, 1);
    }

    /**
     * Convenience method equivalent to {@link #incrementCounter(String)}.
     */
    @Override
    public final void increment(String aspect) {
        incrementCounter(aspect);
    }

    /**
     * Convenience method equivalent to {@link #count(String, long)} with a value of -1.
     */
    @Override
    public final void decrementCounter(String aspect) {
        count(aspect, -1);
    }

    /**
     * Convenience method equivalent to {@link #decrementCounter(String)}.
     */
    @Override
    public final void decrement(String aspect) {
        decrementCounter(aspect);
    }

    /**
     * Convenience method equivalent to {@link #recordGaugeValue(String, long)}.
     */
    @Override
    public final void gauge(String aspect, long value) {
        recordGaugeValue(aspect, value);
    }

    /**
     * Convenience method equivalent to {@link #recordSetEvent(String, String)}.
     */
    @Override
    public final void set(String aspect, String eventName) {
        recordSetEvent(aspect, eventName);
    }

    /**
     * Convenience method equivalent to {@link #recordExecutionTime(String, long)}.
     */
    @Override
    public final void time(String aspect, long timeInMs) {
        recordExecutionTime(aspect, timeInMs);
    }

    @Override
    public void recordExecutionTimeToNow(String aspect, long systemTimeMillisAtStart) {
        time(aspect, Math.max(0, System.currentTimeMillis() - systemTimeMillisAtStart));
    }
}
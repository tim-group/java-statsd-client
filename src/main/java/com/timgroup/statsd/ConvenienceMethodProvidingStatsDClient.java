package com.timgroup.statsd;

public abstract class ConvenienceMethodProvidingStatsDClient implements StatsDClient {

    public ConvenienceMethodProvidingStatsDClient() {
        super();
    }

    /**
     * Convenience method equivalent to {@link #count(String, int)} with a value of 1. 
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
     * Convenience method equivalent to {@link #count(String, int)} with a value of -1. 
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
     * Convenience method equivalent to {@link #recordGaugeValue(String, int)}. 
     */
    @Override
    public final void gauge(String aspect, int value) {
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
     * Convenience method equivalent to {@link #recordExecutionTime(String, int)}. 
     */
    @Override
    public final void time(String aspect, int timeInMs) {
        recordExecutionTime(aspect, timeInMs);
    }

}
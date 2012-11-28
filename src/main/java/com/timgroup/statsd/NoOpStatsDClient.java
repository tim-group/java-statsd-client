package com.timgroup.statsd;

/**
 * A No-Op StatsDClient, which can be substituted in when metrics are not
 * required.
 * 
 * @author Tom Denley
 *
 */
public final class NoOpStatsDClient implements StatsDClient {
    @Override public void stop() { }
    @Override public void count(String aspect, int delta, String[] tags) { }
    @Override public void count(String aspect, int delta) { }
    @Override public void incrementCounter(String aspect, String[] tags) { }
    @Override public void incrementCounter(String aspect) { }
    @Override public void increment(String aspect, String[] tags) { }
    @Override public void increment(String aspect) { }
    @Override public void decrementCounter(String aspect, String[] tags) { }
    @Override public void decrementCounter(String aspect) { }
    @Override public void decrement(String aspect, String[] tags) { }
    @Override public void decrement(String aspect) { }
    @Override public void recordGaugeValue(String aspect, double value, String[] tags) { }
    @Override public void recordGaugeValue(String aspect, double value) { }
    @Override public void gauge(String aspect, double value, String[] tags) { }
    @Override public void gauge(String aspect, double value) { }
    @Override public void recordGaugeValue(String aspect, int value, String[] tags) { }
    @Override public void recordGaugeValue(String aspect, int value) { }
    @Override public void gauge(String aspect, int value, String[] tags) { }
    @Override public void gauge(String aspect, int value) { }
    @Override public void recordExecutionTime(String aspect, int timeInMs, String[] tags) { }
    @Override public void recordExecutionTime(String aspect, int timeInMs) { }
    @Override public void time(String aspect, int value, String[] tags) { }
    @Override public void time(String aspect, int value) { }
    @Override public void recordHistogramValue(String aspect, double value, String[] tags) { }
    @Override public void recordHistogramValue(String aspect, double value) { }
    @Override public void histogram(String aspect, double value, String[] tags) { }
    @Override public void histogram(String aspect, double value) { }
    @Override public void recordHistogramValue(String aspect, int value, String[] tags) { }
    @Override public void recordHistogramValue(String aspect, int value) { }
    @Override public void histogram(String aspect, int value, String[] tags) { }
    @Override public void histogram(String aspect, int value) { }
}

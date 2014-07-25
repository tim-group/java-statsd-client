package com.timgroup.statsd;

/**
 * A No-Op StatsDClient, which can be substituted in when metrics are not
 * required.
 * 
 * @author Tom Denley
 *
 */
public final class NoOpStatsDClient extends ConvenienceMethodProvidingStatsDClient {
    @Override public void stop() { }
    @Override public void count(String aspect, int delta) { }
    @Override public void recordGaugeValue(String aspect, int value) { }
    @Override public void recordGaugeDelta(String aspect, int delta) { }
    @Override public void recordSetEvent(String aspect, String value) { }
    @Override public void recordExecutionTime(String aspect, int timeInMs) { }
}

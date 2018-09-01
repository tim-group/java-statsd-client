package com.timgroup.statsd;

import static com.timgroup.statsd.MessageUtil.stringValueOf;

/**
 * Same as ConvenienceMethodProvidingStatsDClient
 * Created by c6s on 18-9-1
 */
public abstract class PipelineAdapter implements Pipeline {

    public final Pipeline count(String aspect, long delta) {
        return count(aspect, delta, 1.0);
    }

    /**
     * Convenience method equivalent to {@link #count(String, long)} with a value of 1.
     */
    public final Pipeline incrementCounter(String aspect) {
        return count(aspect, 1);
    }

    /**
     * Convenience method equivalent to {@link #incrementCounter(String)}.
     */
    public final Pipeline increment(String aspect) {
        return incrementCounter(aspect);
    }

    /**
     * Convenience method equivalent to {@link #count(String, long)} with a value of -1.
     */
    public final Pipeline decrementCounter(String aspect) {
        return count(aspect, -1);
    }

    /**
     * Convenience method equivalent to {@link #decrementCounter(String)}.
     */
    public final Pipeline decrement(String aspect) {
        return decrementCounter(aspect);
    }

    /**
     * Convenience method equivalent to {@link #recordGaugeValue(String, long)}.
     */
    public final Pipeline gauge(String aspect, long value) {
        return recordGaugeValue(aspect, value);
    }

    /**
     * Convenience method equivalent to {@link #recordGaugeValue(String, double)}.
     */

    public final Pipeline gauge(String aspect, double value) {
        return recordGaugeValue(aspect, value);
    }

    /**
     * Convenience method equivalent to {@link #recordSetEvent(String, String)}.
     */
    public final Pipeline set(String aspect, String eventName) {
        return recordSetEvent(aspect, eventName);
    }

    /**
     * Convenience method equivalent to {@link #recordExecutionTime(String, long)}.
     */
    public final Pipeline time(String aspect, long timeInMs) {
        return recordExecutionTime(aspect, timeInMs);
    }


    public final Pipeline recordExecutionTime(String aspect, long timeInMs) {
        return recordExecutionTime(aspect, timeInMs, 1.0);
    }


    public final Pipeline recordExecutionTimeToNow(String aspect, long systemTimeMillisAtStart) {
        return time(aspect, Math.max(0, System.currentTimeMillis() - systemTimeMillisAtStart));
    }

    public final Pipeline recordGaugeValue(String aspect, long value) {
        return recordGaugeCommon(aspect, Long.toString(value), value < 0, false);
    }

    public final Pipeline recordGaugeValue(String aspect, double value) {
        return recordGaugeCommon(aspect, stringValueOf(value), value < 0, false);
    }

    public final Pipeline recordGaugeDelta(String aspect, long value) {
        return recordGaugeCommon(aspect, Long.toString(value), value < 0, true);
    }

    public final Pipeline recordGaugeDelta(String aspect, double value) {
        return recordGaugeCommon(aspect, stringValueOf(value), value < 0, true);
    }

    abstract Pipeline recordGaugeCommon(String aspect, String value, boolean negative, boolean delta);
}

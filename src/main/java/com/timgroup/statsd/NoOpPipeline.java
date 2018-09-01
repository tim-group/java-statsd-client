package com.timgroup.statsd;

/**
 * Created by c6s on 18-9-1
 */
public class NoOpPipeline extends PipelineAdapter {
    Pipeline recordGaugeCommon(String aspect, String value, boolean negative, boolean delta) {
        return this;
    }

    public Pipeline count(String aspect, long delta, double sampleRate) {
        return this;
    }

    public Pipeline recordSetEvent(String aspect, String eventName) {
        return this;
    }

    public Pipeline recordExecutionTime(String aspect, long timeInMs, double sampleRate) {
        return this;
    }

    public void flush() {

    }
}

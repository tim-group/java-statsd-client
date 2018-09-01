package com.timgroup.statsd;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by c6s on 18-9-1
 */
public class NonBlockingPipeline extends PipelineAdapter {

    private NonBlockingStatsDClient nonBlockingStatsDClient;
    private String prefix;
    private List<String> messageList = new ArrayList<String>();
    private int MTU = 512;
    private boolean closed = false;

    public NonBlockingPipeline(NonBlockingStatsDClient nonBlockingStatsDClient) {
        this.nonBlockingStatsDClient = nonBlockingStatsDClient;
        prefix = nonBlockingStatsDClient.getPrefix();
    }

    public NonBlockingPipeline(NonBlockingStatsDClient nonBlockingStatsDClient, int MTU) {
        this(nonBlockingStatsDClient);
        this.MTU = MTU;
    }

    public Pipeline count(String aspect, long delta, double sampleRate) {
        if (!closed) {
            messageList.add(MessageUtil.makeCountMessage(prefix, aspect, delta, sampleRate));
        }
        return this;
    }

    Pipeline recordGaugeCommon(String aspect, String value, boolean negative, boolean delta) {
        if (!closed) {
            messageList.add(MessageUtil.makeGaugeMessage(prefix, aspect, value, negative, delta));
        }
        return this;
    }

    public Pipeline recordSetEvent(String aspect, String eventName) {
        if (!closed) {
            messageList.add(MessageUtil.makeRecordSetEventMessage(prefix, aspect, eventName));
        }
        return this;
    }

    public Pipeline recordExecutionTime(String aspect, long timeInMs, double sampleRate) {
        if (!closed) {
            messageList.add(MessageUtil.makeRecordExecutionTimeMessage(prefix, aspect, timeInMs, sampleRate));
        }
        return this;
    }

    public void flush() {
        if (closed || messageList.isEmpty()) {
            closed = true;
            return;
        }
        List<String> packets = concatenate();
        for (String p : packets) {
            nonBlockingStatsDClient.send(p);
        }
        closed = true;
    }

    private List<String> concatenate() {
        List<String> result = new ArrayList<String>();
        StringBuilder builder = new StringBuilder(messageList.get(0));
        int sz = builder.length();

        for (int i = 1; i < messageList.size(); i++) {
            String s = messageList.get(i);
            if (sz + 1 + s.length() <= MTU) {
                sz += 1 + s.length();
                builder.append('\n').append(s);
            } else {
                result.add(builder.toString());
                builder = new StringBuilder(s);
                sz = builder.length();
            }
        }


        if (builder.length() != 0) {
            result.add(builder.toString());
        }

        return result;
    }
}

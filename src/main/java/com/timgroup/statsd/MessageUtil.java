package com.timgroup.statsd;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by c6s on 18-9-1
 */
public class MessageUtil {
    private static String messageFor(String prefix, String aspect, String value, String type) {
        return messageFor(prefix, aspect, value, type, 1.0);
    }

    private static String messageFor(String prefix, String aspect, String value, String type, double sampleRate) {
        final String message = prefix + aspect + ':' + value + '|' + type;
        return (sampleRate == 1.0)
                ? message
                : (message + "|@" + stringValueOf(sampleRate));
    }

    static String stringValueOf(double value) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setGroupingUsed(false);
        formatter.setMaximumFractionDigits(19);
        return formatter.format(value);
    }

    static String makeCountMessage(String prefix, String aspect, long delta, double sampleRate) {
        return messageFor(prefix, aspect, Long.toString(delta), "c", sampleRate);
    }

    static String makeGaugeMessage(String prefix, String aspect, String value, boolean negative, boolean delta) {
        StringBuilder message = new StringBuilder();
        if (!delta && negative) {
            message.append(messageFor(prefix, aspect, "0", "g")).append('\n');
        }
        message.append(messageFor(prefix, aspect, (delta && !negative) ? ("+" + value) : value, "g"));
        return message.toString();
    }

    static String makeRecordSetEventMessage(String prefix, String aspect, String eventName) {
        return messageFor(prefix, aspect, eventName, "s");
    }

    static String makeRecordExecutionTimeMessage(String prefix, String aspect, long timeInMs, double sampleRate) {
        return messageFor(prefix, aspect, Long.toString(timeInMs), "ms", sampleRate)
    }
}

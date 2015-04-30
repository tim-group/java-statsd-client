package com.timgroup.statsd;

/**
 * A service check model, which is used to format a service check message
 * sent to the datadog agent
 */
public class ServiceCheck {

    public enum Status {
        OK(0), WARNING(1), CRITICAL(2), UNKNOWN(3);

        private final int val;
        Status(final int val) {
            this.val = val;
        }
    }

    private String name, hostname, message;

    private int checkRunId, timestamp;

    private Status status;

    private String[] tags;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        final ServiceCheck res = new ServiceCheck();

        public Builder withName(final String name) {
            res.name = name;
            return this;
        }

        public Builder withHostname(final String hostname) {
            res.hostname = hostname;
            return this;
        }

        public Builder withMessage(final String message) {
            res.message = message;
            return this;
        }

        public Builder withCheckRunId(final int checkRunId) {
            res.checkRunId = checkRunId;
            return this;
        }

        public Builder withTimestamp(final int timestamp) {
            res.timestamp = timestamp;
            return this;
        }

        public Builder withStatus(final Status status) {
            res.status = status;
            return this;
        }

        public Builder withTags(final String[] tags) {
            res.tags = tags;
            return this;
        }

        public ServiceCheck build() {
            return res;
        }
    }

    private ServiceCheck() {
    }

    public String getName() {
        return name;
    }

    public int getStatus() {
        return status.val;
    }

    public String getMessage() {
        return message;
    }

    public String getEscapedMessage() {
        return message.replace("\n", "\\n").replace("m:", "m\\:");
    }

    public String getHostname() {
        return hostname;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public String[] getTags() {
        return tags;
    }
}

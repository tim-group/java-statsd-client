package com.timgroup.statsd;

/**
 * A service check model, which is used to format a service check message
 * sent to the datadog agent
 */
public class ServiceCheck {
    public static final int OK = 0;
    public static final int WARNING = 1;
    public static final int CRITICAL = 2;
    public static final int UNKNOWN = 3;

    private String name, hostname, message;

    private int status, checkRunId, timestamp;

    private String[] tags;

    /**
     */
    public ServiceCheck() {
    }

    /**
     * @param name
     * @param status
     */
    public ServiceCheck(String name, int status) {
        this(name, status, null, null, null);
    }

    public ServiceCheck(String name, int status, String message, String[] tags) {
        this(name, status, message, null, tags);
    }

    public ServiceCheck(String name, int status, String message, String hostname, String[] tags) {
        this.name = name;
        this.status = status;
        this.message = message;
        this.hostname = hostname;
        this.tags = tags;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return
     */
    public String getEscapedMessage() {
        return message.replace("\n", "\\n").replace("m:", "m\\:");
    }

    /**
     *
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @param hostname
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * @return
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp
     */
    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return
     */
    public String[] getTags() {
        return tags;
    }

    /**
     * @param tags
     */
    public void setTags(String... tags) {
        this.tags = tags;
    }
}

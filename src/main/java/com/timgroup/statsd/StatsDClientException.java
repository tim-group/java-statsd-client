package com.timgroup.statsd;


/**
 * Signals that an exception has occurred when trying to start the
 * StatsD client
 * 
 * @author Tom Denley
 * 
 */
public final class StatsDClientException extends RuntimeException {

    private static final long serialVersionUID = 3186887620964773839L;

    public StatsDClientException() {
        super();
    }

    public StatsDClientException(String message, Throwable cause) {
        super(message, cause);
    }
}

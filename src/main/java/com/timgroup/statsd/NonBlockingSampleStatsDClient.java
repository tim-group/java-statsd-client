package com.timgroup.statsd;

public class NonBlockingSampleStatsDClient extends NonBlockingStatsDClient implements SampleStatsDClient {
    
    /**
     * Create a new StatsD client communicating with a StatsD instance on the
     * specified host and port. All messages send via this client will have
     * their keys prefixed with the specified string. The new client will
     * attempt to open a connection to the StatsD server immediately upon
     * instantiation, and may throw an exception if that a connection cannot
     * be established. Once a client has been instantiated in this way, all
     * exceptions thrown during subsequent usage are consumed, guaranteeing
     * that failures in metrics will not affect normal code execution.
     * 
     * @param prefix
     *     the prefix to apply to keys sent via this client
     * @param hostname
     *     the host name of the targeted StatsD server
     * @param port
     *     the port of the targeted StatsD server
     * @throws StatsDClientException
     *     if the client could not be started
     */
    public NonBlockingSampleStatsDClient(String prefix, String hostname, int port) throws StatsDClientException {
        super(prefix, hostname, port, NO_OP_HANDLER);
    }
    
    /**
     * Create a new StatsD client communicating with a StatsD instance on the
     * specified host and port. All messages send via this client will have
     * their keys prefixed with the specified string. The new client will
     * attempt to open a connection to the StatsD server immediately upon
     * instantiation, and may throw an exception if that a connection cannot
     * be established. Once a client has been instantiated in this way, all
     * exceptions thrown during subsequent usage are passed to the specified
     * handler and then consumed, guaranteeing that failures in metrics will
     * not affect normal code execution.
     * 
     * @param prefix
     *     the prefix to apply to keys sent via this client
     * @param hostname
     *     the host name of the targeted StatsD server
     * @param port
     *     the port of the targeted StatsD server
     * @param errorHandler
     *     handler to use when an exception occurs during usage
     * @throws StatsDClientException
     *     if the client could not be started
     */
    public NonBlockingSampleStatsDClient(String prefix, String hostname, int port, StatsDClientErrorHandler errorHandler) throws StatsDClientException {
        super(prefix, hostname, port, errorHandler);
    }

	@Override
	public void count(String aspect, int delta, double rate) {
		if(Math.random() < rate) {
			send(String.format("%s.%s:%d|c|@%f", prefix, aspect, delta, rate));
		}
	}

	@Override
	public void incrementCounter(String aspect, double rate) {
		if(Math.random() < rate) {
			send(String.format("%s.%s:%d|c|@%f", prefix, aspect, 1, rate));
		}
	}

	@Override
	public void decrementCounter(String aspect, double rate) {
		if(Math.random() < rate) {
			send(String.format("%s.%s:%d|c|@%f", prefix, aspect, -1, rate));
		}
	}
}

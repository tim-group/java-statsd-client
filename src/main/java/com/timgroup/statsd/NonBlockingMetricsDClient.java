package com.timgroup.statsd;

/**
 * Created with IntelliJ IDEA.
 * User: pierce
 * Date: 10/24/12
 * Time: 5:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class NonBlockingMetricsDClient extends NonBlockingStatsDClient
	implements MetricsDClient
{
	/**
	 * Create a new MetricsD client communicating with a StatsD instance on the
	 * specified host and port. All messages send via this client will have
	 * their keys prefixed with the specified string. The new client will
	 * attempt to open a connection to the StatsD server immediately upon
	 * instantiation, and may throw an exception if that a connection cannot
	 * be established. Once a client has been instantiated in this way, all
	 * exceptions thrown during subsequent usage are consumed, guaranteeing
	 * that failures in metrics will not affect normal code execution.
	 *
	 * @param prefix   the prefix to apply to keys sent via this client
	 * @param hostname the host name of the targeted StatsD server
	 * @param port     the port of the targeted StatsD server
	 * @throws com.timgroup.statsd.StatsDClientException
	 *          if the client could not be started
	 */
	public NonBlockingMetricsDClient(String prefix, String hostname, int port) throws StatsDClientException
	{
		super( prefix, hostname, port );
	}

	/**
	 * Create a new MetricsD client communicating with a StatsD instance on the
	 * specified host and port. All messages send via this client will have
	 * their keys prefixed with the specified string. The new client will
	 * attempt to open a connection to the StatsD server immediately upon
	 * instantiation, and may throw an exception if that a connection cannot
	 * be established. Once a client has been instantiated in this way, all
	 * exceptions thrown during subsequent usage are passed to the specified
	 * handler and then consumed, guaranteeing that failures in metrics will
	 * not affect normal code execution.
	 *
	 * @param prefix       the prefix to apply to keys sent via this client
	 * @param hostname     the host name of the targeted StatsD server
	 * @param port         the port of the targeted StatsD server
	 * @param errorHandler handler to use when an exception occurs during usage
	 * @throws com.timgroup.statsd.StatsDClientException
	 *          if the client could not be started
	 */
	public NonBlockingMetricsDClient(String prefix, String hostname, int port, StatsDClientErrorHandler errorHandler) throws StatsDClientException
	{
		super( prefix, hostname, port, errorHandler );
	}

	/**
	 * Records a value for the specific named histogram.
	 * <p/>
	 * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
	 *
	 * @param aspect the name of the histogram
	 * @param value  the value for the histogram
	 */
	public void recordHistogram(String aspect, int value)
	{
		send(String.format("%s.%s:%d|h", prefix, aspect, value));
	}

	/**
	 * Records a value of 1 for the specific named meter.
	 * <p/>
	 * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
	 *
	 * @param aspect the name of the meter
	 */
	public void recordMark(String aspect)
	{
		send(String.format("%s.%s", prefix, aspect));
	}
}

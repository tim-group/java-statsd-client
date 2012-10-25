package com.timgroup.statsd;

/**
 * Describes a client connection to a MetricsD server, which may be used to post metrics
 * in the form of counters, timers, gauges, histograms, and meters.
 *
 * <p>Three key methods are provided for the submission of data-points for the application under
 * scrutiny:
 * <ul>
 *   <li>{@link #incrementCounter} - adds one to the value of the specified named counter</li>
 *   <li>{@link #recordGaugeValue} - records the latest fixed value for the specified named gauge</li>
 *   <li>{@link #recordExecutionTime} - records an execution time in milliseconds for the specified named operation</li>
 *   <li>{@link #recordHistogram} - records a histogram value</li>
 *   <li>{@link #recordMark} - record a meter mark</li>
 * </ul>
 *
 * @author Tom Denley
 *
 */
public interface MetricsDClient extends StatsDClient
{

	/**
	 * Records a value for the specific named histogram.
	 *
	 * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
	 *
	 * @param aspect
	 *     the name of the histogram
	 * @param value
	 *     the value for the histogram
	 */
	void recordHistogram(String aspect, int value);

	/**
	 * Records a value of 1 for the specific named meter.
	 *
	 * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
	 *
	 * @param aspect
	 *     the name of the meter
	 */
	void recordMark(String aspect);

}

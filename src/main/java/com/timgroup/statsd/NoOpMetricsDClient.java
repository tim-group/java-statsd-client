package com.timgroup.statsd;

/**
 * Created with IntelliJ IDEA.
 * User: pierce
 * Date: 10/24/12
 * Time: 5:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class NoOpMetricsDClient implements MetricsDClient
{

	
	@Override
	public void recordHistogram(String aspect, int value)
	{
	}

	
	@Override
	public void recordMark(String aspect)
	{
	}

	
	@Override
	public void stop()
	{
		//// do nothing
	}

	
	@Override
	public void count(String aspect, int delta)
	{
		//// do nothing
	}

	
	@Override
	public void incrementCounter(String aspect)
	{
		//// do nothing
	}

	
	@Override
	public void increment(String aspect)
	{
		//// do nothing
	}

	
	@Override
	public void decrementCounter(String aspect)
	{
		//// do nothing
	}

	
	@Override
	public void decrement(String aspect)
	{
		//// do nothing
	}

	
	@Override
	public void recordGaugeValue(String aspect, int value)
	{
		//// do nothing
	}

	
	@Override
	public void gauge(String aspect, int value)
	{
		//// do nothing
	}

	
	@Override
	public void recordExecutionTime(String aspect, int timeInMs)
	{
		//// do nothing
	}

	
	@Override
	public void time(String aspect, int value)
	{
		//// do nothing
	}
}

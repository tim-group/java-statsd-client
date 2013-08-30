package com.timgroup.statsd;

import com.timgroup.statsd.StatsDClient;

public interface SampleStatsDClient extends StatsDClient {
    /**
     * Adjusts the specified counter by a given delta.
     * 
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     * 
     * @param aspect
     *     the name of the counter to adjust
     * @param delta
     *     the amount to adjust the counter by
     *  @param rate {@code double} greater than or equal
     * 	to {@code 0.0} and less than {@code 1.0}
     */
    void count(String aspect, int delta, double rate);

    /**
     * Increments the specified counter by one.
     * 
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     * 
     * @param aspect
     *     the name of the counter to increment
     * @param rate {@code double} greater than or equal
     * to {@code 0.0} and less than {@code 1.0}
     */
    void incrementCounter(String aspect, double rate);

    /**
     * Decrements the specified counter by one.
     * 
     * <p>This method is non-blocking and is guaranteed not to throw an exception.</p>
     * 
     * @param aspect
     *     the name of the counter to decrement
     * @param rate {@code double} greater than or equal
     * to {@code 0.0} and less than {@code 1.0}
     */
    void decrementCounter(String aspect, double rate);
}

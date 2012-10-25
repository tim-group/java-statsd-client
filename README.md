java-statsd-client
==================

A statsd client library implemented in Java.  Allows for Java applications to easily communicate with statsd.

Also allows for Java applications to easily communicate with [MetricsD](https://github.com/mojodna/metricsd), which provides
a superset of the statsd commands based on the [Metrics](http://metrics.codahale.com) library. This can be useful for aggregating 
data from multiple hosts before sending the data to [graphite](http://graphite.wikidot.com).

Downloads
---------
The client jar is distributed via maven central, and can be downloaded [here](http://search.maven.org/#search%7Cga%7C1%7Cg%3Acom.timgroup%20a%3Ajava-statsd-client).

```xml
<dependency>
    <groupId>com.timgroup</groupId>
    <artifactId>java-statsd-client</artifactId>
    <version>2.1.0</version>
</dependency>
```

StatsD Usage
-----
```java
import com.timgroup.statsd.StatsDClient;
import com.timgroup.statsd.NonBlockingStatsDClient;

public class Foo {
  private static final StatsDClient statsd = new NonBlockingStatsDClient("my.prefix", "statsd-host", 8125);

  public static final void main(String[] args) {
    statsd.incrementCounter("bar");
    statsd.recordGaugeValue("baz", 100);
    statsd.recordExecutionTime("bag", 25);
  }
}
```

MetricsD Usage
-----
```java
import com.timgroup.statsd.MetricsDClient;
import com.timgroup.statsd.NonBlockingMetricsDClient;

public class Foo {
  private static final MetricsDClient statsd = new NonBlockingMetricsDClient("my.prefix", "statsd-host", 8125);

  public static final void main(String[] args) {
    statsd.incrementCounter("sample.bar");
    statsd.recordGaugeValue("sample.baz", 100);
    statsd.recordExecutionTime("sample.bag", 25);
    statsd.recordHistogram("sample.sample", 25);
    statsd.recordMark("sample.meter");
  }
}
```


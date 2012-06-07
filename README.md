java-statsd-client
==================

A statsd client library implemented in Java.  Allows for Java applications to easily communicate with statsd.

Usage
=====
```java
import com.timgroup.statsd.StatsDClient;

public class Foo {
  private final StatsDClient statsd = new StatsDClient("foo.test", "localhost", 8125);

  public static final void main(String[] args) {
    statsd.incrementCounter("bar");
    statsd.recordGaugeValue("baz", 100);
    statsd.recordExecutionTime("bag", 25);
  }
}
```


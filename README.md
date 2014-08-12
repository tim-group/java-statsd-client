java-statsd-client
==================

[![Build Status](https://travis-ci.org/tim-group/java-statsd-client.svg?branch=master)](https://travis-ci.org/tim-group/java-statsd-client)

A statsd client library implemented in Java.  Allows for Java applications to easily communicate with statsd.

Downloads
---------
The client jar is distributed via maven central, and can be downloaded [here](http://search.maven.org/#search%7Cga%7C1%7Cg%3Acom.timgroup%20a%3Ajava-statsd-client).

```xml
<dependency>
    <groupId>com.timgroup</groupId>
    <artifactId>java-statsd-client</artifactId>
    <version>3.0.1</version>
</dependency>
```

Usage
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
    statsd.recordSetEvent("qux", "one");
  }
}
```


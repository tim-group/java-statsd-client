package com.timgroup.statsd;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class EventTest {
    @Test
    public void builds() {
        final Event event = Event.builder()
                .withTitle("title1")
                .withText("text1")
                .withDate(1234)
                .withHostname("host1")
                .withPriority(Event.Priority.LOW)
                .withAggregationKey("key1")
                .withAlertType(Event.AlertType.ERROR)
                .build();

        assertEquals("title1", event.getTitle());
        assertEquals("text1", event.getText());
        assertEquals(1234, event.getMillisSinceEpoch());
        assertEquals("host1", event.getHostname());
        assertEquals("low", event.getPriority());
        assertEquals("key1", event.getAggregationKey());
        assertEquals("error", event.getAlertType());
    }

    @Test
    public void builds_with_defaults() {
        final Event event = Event.builder()
                .withTitle("title1")
                .withText("text1")
                .build();

        assertEquals("title1", event.getTitle());
        assertEquals("text1", event.getText());
        assertEquals(-1, event.getMillisSinceEpoch());
        assertEquals(null, event.getHostname());
        assertEquals(null, event.getPriority());
        assertEquals(null, event.getAggregationKey());
        assertEquals(null, event.getAlertType());
    }

    @Test (expected = IllegalStateException.class)
    public void fails_without_title() {
        Event.builder().withText("text1")
                .withDate(1234)
                .withHostname("host1")
                .withPriority(Event.Priority.LOW)
                .withAggregationKey("key1")
                .withAlertType(Event.AlertType.ERROR)
                .build();
    }

    @Test (expected = IllegalStateException.class)
    public void fails_without_text() {
        Event.builder().withTitle("title1")
                .withDate(1234)
                .withHostname("host1")
                .withPriority(Event.Priority.LOW)
                .withAggregationKey("key1")
                .withAlertType(Event.AlertType.ERROR)
                .build();
    }

    @Test
    public void builds_with_date() {
        final long expectedMillis = 1234567000;
        final Date date = new Date(expectedMillis);
        final Event event = Event.builder()
                .withTitle("title1")
                .withText("text1")
                .withDate(date)
                .build();

        assertEquals("title1", event.getTitle());
        assertEquals("text1", event.getText());
        assertEquals(expectedMillis, event.getMillisSinceEpoch());
    }
}

package org.example.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.time.Clock;
import java.time.temporal.ChronoField;

public class MicrosecondsConverter extends ClassicConverter {
    static Clock clock = Clock.systemDefaultZone();
    static ChronoField chronoField = ChronoField.MICRO_OF_SECOND;

    @Override
    public String convert(ILoggingEvent event) {
        return Long.toString(clock.instant().get(chronoField) % 1000);
    }
}

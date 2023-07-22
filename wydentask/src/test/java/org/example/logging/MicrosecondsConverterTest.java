package org.example.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoField;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MicrosecondsConverterTest {

    @Mock
    private ILoggingEvent mockEvent;

    @InjectMocks
    private MicrosecondsConverter converter;

    @Test
    public void testConvert() {
        // given
        Clock fixedClock = Clock.fixed(Instant.parse("2023-07-22T11:00:00.000Z"), ZoneId.systemDefault());
        MicrosecondsConverter.clock = fixedClock;
        long expectedMicroseconds = fixedClock.instant().get(ChronoField.MICRO_OF_SECOND) % 1000;

        // when
        String convertedMicroseconds = converter.convert(mockEvent);

        // then
        assertEquals(String.valueOf(expectedMicroseconds), convertedMicroseconds);
    }
}
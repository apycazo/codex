package apycazo.codex.rest.common.data;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JUnitPlatform.class)
class TimeValueTest {

  @Test
  void test_unit_to_string() {
    TimeValue unit = new TimeValue(10L, ChronoUnit.MINUTES);
    assertEquals("10m", unit.toString(false));
    assertEquals("10Minutes", unit.toString(true));
    unit = unit.withUnit(ChronoUnit.SECONDS);
    assertEquals("10s", unit.toString());
    assertEquals("10Seconds", unit.toString(true));
    unit = unit.withUnit(ChronoUnit.HOURS);
    assertEquals("10h", unit.toString());
    assertEquals("10Hours", unit.toString(true));
    unit = unit.withUnit(ChronoUnit.DAYS);
    assertEquals("10d", unit.toString());
    assertEquals("10Days", unit.toString(true));
    unit = unit.withUnit(ChronoUnit.WEEKS);
    assertEquals("10w", unit.toString());
    assertEquals("10Weeks", unit.toString(true));
    unit = unit.withUnit(ChronoUnit.MONTHS);
    assertEquals("10M", unit.toString());
    assertEquals("10Months", unit.toString(true));
    unit = unit.withUnit(ChronoUnit.YEARS);
    assertEquals("10y", unit.toString());
    assertEquals("10Years", unit.toString(true));
  }
}
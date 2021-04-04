package apycazo.codex.rest.common.data;

import lombok.Getter;
import lombok.With;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class TimeValue {

  @With
  long value;
  @With
  ChronoUnit unit;

  public TimeValue(long value, ChronoUnit unit) {
    this.value = value;
    this.unit = unit;
  }

  public boolean isValid() {
    return value >= 0 && unit != null;
  }

  @Override
  public String toString() {
    return toString(false);
  }

  public String toString(boolean fullName) {
    return String.format("%d%s", value, fullName ? unit.toString() : getUnitName(unit));
  }

  public TimeValue parse(String s) throws NumberFormatException {
    Pattern pattern = Pattern.compile("^(\\d+)([A-z]+)$", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(s);
    if (matcher.find()) {
      String rawValue = matcher.group(0);
      String rawUnit = matcher.group(1);
      long value = Long.parseLong(rawValue);
      ChronoUnit unit = getUnit(rawUnit);
      if (unit == null) {
        throw new NumberFormatException("Invalid unit type: " + rawUnit);
      } else {
        return new TimeValue(value, unit);
      }
    } else {
      return new TimeValue(0L, null);
    }
  }

  private ChronoUnit getUnit(String s) {
    if (s == null || s.trim().isEmpty()) {
      return null;
    } else {
      return Arrays.stream(ChronoUnit.values())
        .filter(v -> v.name().equalsIgnoreCase(s) || getUnitName(v).equalsIgnoreCase(s))
        .findAny().orElse(null);
    }
  }

  private String getUnitName(ChronoUnit unit) {
    switch (unit) {
      case MILLIS:
        return "ms";
      case SECONDS:
        return "s";
      case MINUTES:
        return "m";
      case HOURS:
        return "h";
      case DAYS:
        return "d";
      case WEEKS:
        return "w";
      case MONTHS:
        return "M";
      case YEARS:
        return "y";
      case FOREVER:
        return "f";
      default:
        return unit.name();
    }
  }
}

package org.goplanit.utils;

import org.goplanit.utils.time.ExtendedLocalTime;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ExtendedLocalTimeTest {

  @Test
  public void extendedLocalTimeTest(){
    var beforeMidnight = ExtendedLocalTime.of("23:59:59");
    assertThat(beforeMidnight.exceedsSingleDay(), CoreMatchers.is(false));
    assertThat(beforeMidnight.toString(), CoreMatchers.is("23:59:59"));
    assertThat(beforeMidnight.asLocalTimeAfterMidnight(), CoreMatchers.is((LocalTime)null));

    var afterMidnight = ExtendedLocalTime.of("24:00:00");
    assertThat(afterMidnight.exceedsSingleDay(), CoreMatchers.is(true));
    assertThat(afterMidnight.toString(), CoreMatchers.is("24:00:00"));
    assertThat(afterMidnight.asLocalTimeAfterMidnight().format(DateTimeFormatter.ISO_LOCAL_TIME), CoreMatchers.is("00:00:00"));

    var newTime = afterMidnight.minus(beforeMidnight);
    assertThat(newTime, notNullValue());
    assertThat(newTime, equalTo(ExtendedLocalTime.of(LocalTime.of(0,0,1))));
    newTime = beforeMidnight.minus(afterMidnight);
    assertThat(newTime, nullValue());

    newTime = beforeMidnight.plus(beforeMidnight);
    assertThat(newTime, notNullValue());
    assertThat(newTime, equalTo(ExtendedLocalTime.of("47:59:58")));
    assertThat(newTime.asLocalTimeAfterMidnight().format(DateTimeFormatter.ISO_LOCAL_TIME), CoreMatchers.is("23:59:58"));

    afterMidnight = ExtendedLocalTime.of("33:33:33");
    assertThat(afterMidnight.exceedsSingleDay(), CoreMatchers.is(true));
    assertThat(afterMidnight.toString(), CoreMatchers.is("33:33:33"));

    var afterNextMidnight = ExtendedLocalTime.of("48:00:00");
    assertThat(afterNextMidnight, nullValue());

    assertThat(beforeMidnight.isBefore(afterMidnight), CoreMatchers.is(true));
    assertThat(afterMidnight.isBefore(beforeMidnight), CoreMatchers.is(false));
    assertThat(afterMidnight.isAfter(beforeMidnight), CoreMatchers.is(true));
    assertThat(beforeMidnight.isAfter(afterMidnight), CoreMatchers.is(false));

    assertThat(beforeMidnight.compareTo(afterMidnight), CoreMatchers.is(-1));
    assertThat(beforeMidnight.compareTo(beforeMidnight), CoreMatchers.is(0));
    assertThat(beforeMidnight.compareTo(ExtendedLocalTime.of("23:59:59")), CoreMatchers.is(0));
    assertThat(beforeMidnight.equals(ExtendedLocalTime.of("23:59:59")), CoreMatchers.is(true));

  }
}

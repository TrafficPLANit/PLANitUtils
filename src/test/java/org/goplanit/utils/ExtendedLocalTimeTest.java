package org.goplanit.utils;

import org.goplanit.utils.time.ExtendedLocalTime;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.time.LocalTime;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ExtendedLocalTimeTest {

  @Test
  public void extendedLocalTimeTest(){
    var beforeMidnight = ExtendedLocalTime.of("23:59:59");
    assertThat(beforeMidnight.exceedsSingleDay(), CoreMatchers.is(false));
    assertThat(beforeMidnight.toString(), CoreMatchers.is("23:59:59"));

    var afterMidnight = ExtendedLocalTime.of("24:00:00");
    assertThat(afterMidnight.exceedsSingleDay(), CoreMatchers.is(true));
    assertThat(afterMidnight.toString(), CoreMatchers.is("24:00:00"));

    var newTime = afterMidnight.minus(beforeMidnight);
    assertThat(newTime, notNullValue());
    assertThat(newTime, equalTo(ExtendedLocalTime.of(LocalTime.of(0,0,1))));
    newTime = beforeMidnight.minus(afterMidnight);
    assertThat(newTime, nullValue());

    afterMidnight = ExtendedLocalTime.of("33:33:33");
    assertThat(afterMidnight.exceedsSingleDay(), CoreMatchers.is(true));
    assertThat(afterMidnight.toString(), CoreMatchers.is("33:33:33"));

    var afterNextMidnight = ExtendedLocalTime.of("48:00:00");
    assertThat(afterNextMidnight, nullValue());


  }
}

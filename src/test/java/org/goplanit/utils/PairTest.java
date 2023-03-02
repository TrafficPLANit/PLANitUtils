package org.goplanit.utils;

import org.goplanit.utils.misc.ComparablePair;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.time.ExtendedLocalTime;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class PairTest {

  @Test
  public void pairTest(){

    assertThat(Pair.of(1,2).equals(Pair.of(1,3)), CoreMatchers.is(false));
    assertThat(Pair.of(1,2).equals(Pair.of(1,2)), CoreMatchers.is(true));
  }

  @Test
  public void ComparablePairTest(){

    assertThat(ComparablePair.of(1,2).compareTo(ComparablePair.of(1,3)), CoreMatchers.is(-1));
    assertThat(ComparablePair.of(1,2).compareTo(ComparablePair.of(1,2)), CoreMatchers.is(0));
    assertThat(ComparablePair.of(1,2).compareTo(ComparablePair.of(1,1)), CoreMatchers.is(1));

    assertThat(ComparablePair.of(1,2).compareTo(ComparablePair.of(null,1)), CoreMatchers.is(1));
    assertThat(ComparablePair.of(1,2).compareTo(ComparablePair.of(1,null)), CoreMatchers.is(1));

    assertThat(ComparablePair.of(null,2).compareTo(ComparablePair.of(1,2)), CoreMatchers.is(-1));
    assertThat(ComparablePair.of(1,null).compareTo(ComparablePair.of(1,2)), CoreMatchers.is(-1));

    assertThat(ComparablePair.of(1,null).compareTo(ComparablePair.of(1,null)), CoreMatchers.is(0));
    assertThat(ComparablePair.of(null,null).compareTo(ComparablePair.of(null,null)), CoreMatchers.is(0));

  }
}

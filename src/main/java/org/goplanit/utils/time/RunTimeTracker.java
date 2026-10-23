package org.goplanit.utils.time;

import java.util.concurrent.atomic.LongAdder;

/**
 * Track run times for a single entity in total and per iteration
 */
public class RunTimeTracker {

  private final String name;

  private final LongAdder totalRunTime = new LongAdder();

  private final LongAdder iterationRunTime = new LongAdder();

  protected RunTimeTracker(String name){
    this.name = name;
  }

  protected RunTimeTracker(RunTimeTracker other){
    this.name = other.name;
    totalRunTime.add(other.totalRunTime.longValue());
    iterationRunTime.add(other.iterationRunTime.longValue());
  }

  /**
   * Factory method
   *
   * @param name to use for tracker
   * @return new tracker
   */
  public static RunTimeTracker of(String name) {
    return new RunTimeTracker(name);
  }

  public void addTimeInMillis(long timeInMillis){
    totalRunTime.add(timeInMillis);
    iterationRunTime.add(timeInMillis);
  }

  public long getIterationTimeInMillis(){
    return iterationRunTime.longValue();
  }

  public long getTotalTimeInMillis(){
    return totalRunTime.longValue();
  }

  public void resetIterationTime(){
    iterationRunTime.reset();
  }

  public void reset() {
    resetIterationTime();
    totalRunTime.reset();
  }

  /**
   * perform shallow clone
   *
   * @return copy
   */
  public RunTimeTracker shallowClone() {
    return new RunTimeTracker(this);
  }

  /**
   * perform deep clone
   *
   * @return copy
   */
  public RunTimeTracker deepClone() {
    return shallowClone();
  }
}

package org.goplanit.utils.time;

import java.util.TreeMap;

/**
 * Convenience class for tracking run times by category and by iteration
 */
public class RunTimesTracker {

  private TreeMap<String, RunTimeTracker> trackerByType = new TreeMap<>();

  /**
   * Default constructor
   */
  protected RunTimesTracker(){

  }

  /**
   * Copy  constructor
   *
   * @param other to copy
   * @param deepCopy flag
   */
  protected RunTimesTracker(RunTimesTracker other, boolean deepCopy){
    if(!deepCopy){
      trackerByType.putAll(other.trackerByType);
    }else{
      other.trackerByType.forEach( (k,v) -> trackerByType.put( k, v.deepClone()));
    }
  }

  /** suggested type for tracking run times - general */
  public static final String GENERAL = "general";

  public RunTimeTracker registerNew(String type){
    var newTracker = RunTimeTracker.of(type);
    trackerByType.put(type, newTracker);
    return newTracker;
  }

  public RunTimeTracker get(String type){
    return trackerByType.get(type);
  }

  public void addTime(String type, long millis){
    trackerByType.get(type).addTimeInMillis(millis);
  }

  public void reset(){
    trackerByType.forEach( (k,v) -> reset(k));
  }

  public void reset(String type){
    var entry = trackerByType.get(type);
    if(entry != null){
      entry.reset();
    }
  }

  public static RunTimesTracker create(){
    return new RunTimesTracker();
  }

  /**
   * perform shallow clone
   *
   * @return copy
   */
  public RunTimesTracker shallowClone() {
    return new RunTimesTracker(this, false);
  }

  /**
   * perform deep clone
   *
   * @return copy
   */
  public RunTimesTracker deepClone() {
    return  new RunTimesTracker(this, true);
  }
}

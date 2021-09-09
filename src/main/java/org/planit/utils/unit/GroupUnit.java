package org.planit.utils.unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.planit.utils.exceptions.PlanItException;

/** A combined unit consisting of multiple simple (single) units
 * 
 * @author markr
 *
 */
public class GroupUnit implements Unit {

  /**
   * track numerator units
   */
  private final List<SimpleUnit> numeratorUnits;

  /**
   * track denominator units
   */  
  private final List<SimpleUnit> denominatorsUnits = new ArrayList<SimpleUnit>();
  
  /** constructor 
   * @param numeratorUnits
   */
  protected GroupUnit(SimpleUnit... numeratorUnits) {
    this.numeratorUnits = Arrays.asList(numeratorUnits);
  }
  
  /** constructor 
   * @param numeratorUnits
   */
  protected GroupUnit(GroupUnit other, SimpleUnit... denominatorUnits) {
    this.numeratorUnits = new ArrayList<SimpleUnit>(other.numeratorUnits);
  }  
  
  /** Factory method to create a new groupUnit with this unit's numerator and additionally added denominator units, e.g., KM per Hour
   * 
   * @param denominatorUnits
   * @return newly combined group unit
   */
  public GroupUnit per(SimpleUnit... denominatorUnits) {
    return new GroupUnit(this, denominatorUnits);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isCombinedUnit() {
    return true;
  }  
  
  /** All registered units in the numerator
   * 
   * @return numerator units
   */
  protected List<SimpleUnit> getNumeratorUnitTypes(){
    return numeratorUnits;
  }

  /** All registered units in the denominator
   * 
   * @return numerator units
   */  
  protected List<SimpleUnit> getDenominatorUnitTypes(){
    return denominatorsUnits;
  }  
  
  /**
   * {@inheritDoc}
   */
  @Override
  public double convertTo(Unit to, double value) throws PlanItException {
    GroupUnit toUnit = (GroupUnit)to;
    
    double numeratorMultiplier = 1;
    for(SimpleUnit simpleUnit : getNumeratorUnitTypes()) {
      numeratorMultiplier  *= simpleUnit.convertTo(toUnit, numeratorMultiplier );
    }
    double denominatorMultiplier = 1;
    for(SimpleUnit simpleUnit : getDenominatorUnitTypes()) {
      denominatorMultiplier  *= simpleUnit.convertTo(toUnit, denominatorMultiplier );
    }   
    return (value * numeratorMultiplier)/denominatorMultiplier;
  }  
      
  /**
   * {@inheritDoc}
   */  
  @Override
  public boolean canConvertTo(Unit to) {
    if(!isCombinedUnit()) {
      return false;
    }
    
    GroupUnit toUnit = ((GroupUnit)to);
    if(getNumeratorUnitTypes().size()!=toUnit.getNumeratorUnitTypes().size()) {
      return false;
    }
    if(getDenominatorUnitTypes().size()!=toUnit.getDenominatorUnitTypes().size()) {
      return false;
    }
    int index=0;
    for(Iterator<SimpleUnit> iter = getNumeratorUnitTypes().iterator();iter.hasNext();++index){
      if(!iter.next().canConvertTo(toUnit.numeratorUnits.get(index))){
        return false;
      }
    }
    index=0;
    for(Iterator<SimpleUnit> iter = getDenominatorUnitTypes().iterator();iter.hasNext();++index){
      if(!iter.next().canConvertTo(toUnit.denominatorsUnits.get(index))){
        return false;
      }
    } 
    return true;
  }  
  
  /**
   * {@inheritDoc}
   */  
  @Override
  public String toString() {
    return numeratorUnits.stream().map( unit -> unit.toString()).collect(Collectors.joining("*"))
        +"/"+
        denominatorsUnits.stream().map( unit -> unit.toString()).collect(Collectors.joining("*"));
  }
  
}


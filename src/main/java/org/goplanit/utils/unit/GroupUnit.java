package org.goplanit.utils.unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.goplanit.utils.exceptions.PlanItException;

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
  private final List<SimpleUnit> denominatorsUnits;
  
  /** Constructor
   *  
   * @param numeratorUnits to use
   */
  protected GroupUnit(SimpleUnit... numeratorUnits) {
    this.numeratorUnits = Arrays.asList(numeratorUnits);
    this.denominatorsUnits = null;
  }
  
  /** Constructor
   *  
   * @param other to copy numerator units from
   * @param denominatorUnits to use
   */
  protected GroupUnit(GroupUnit other, SimpleUnit... denominatorUnits) {
    this.numeratorUnits = new ArrayList<SimpleUnit>(other.numeratorUnits);
    if(denominatorUnits!=null) {
      this.denominatorsUnits = List.of(denominatorUnits);
    }else {
      this.denominatorsUnits = null;
    }
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
    Iterator<SimpleUnit> iter = getNumeratorUnitTypes().iterator();
    Iterator<SimpleUnit> otherIter = toUnit.getNumeratorUnitTypes().iterator();
    while(iter.hasNext()) {
      numeratorMultiplier *= iter.next().convertTo(otherIter.next(),numeratorMultiplier);
    }  
    
    double denominatorMultiplier = 1;
    iter = getDenominatorUnitTypes().iterator();
    otherIter = toUnit.getDenominatorUnitTypes().iterator();
    while(iter.hasNext()) {
      denominatorMultiplier *= iter.next().convertTo(otherIter.next(),denominatorMultiplier);
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
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
        return false;
    }
    if (getClass() != o.getClass()) {
      return false;
    }      
    GroupUnit otherUnit = (GroupUnit) o;
    if(!this.canConvertTo(otherUnit)) {
      return false;
    }
    Iterator<SimpleUnit> iter = getDenominatorUnitTypes().iterator();
    Iterator<SimpleUnit> otherIter = otherUnit.getDenominatorUnitTypes().iterator();
    while(iter.hasNext()) {
      if(!iter.next().equals(otherIter.next())) {
        return false;
      }
    }
    iter = getNumeratorUnitTypes().iterator();
    otherIter = otherUnit.getNumeratorUnitTypes().iterator();
    while(iter.hasNext()) {
      if(!iter.next().equals(otherIter.next())) {
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


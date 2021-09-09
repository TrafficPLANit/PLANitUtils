package org.planit.utils.unit;

public enum UnitType {

  NONE(UnitGroup.NONE,""),
  VEH(UnitGroup.VEHICLES,"VEH"),
  PCU(UnitGroup.VEHICLES,"PCU"),
  KM(UnitGroup.DISTANCE,"KM"),
  METER(UnitGroup.DISTANCE,"M"),     
  HOUR(UnitGroup.TIME,"H"),
  SECOND(UnitGroup.TIME,"S"),
  MINUTE(UnitGroup.TIME,"VEH"),
  SRS(UnitGroup.SRS,"SRS");
  
  protected UnitGroup group;
  
  protected String name;
  
  UnitType(final UnitGroup group, final String name) {
    this.group = group;
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public UnitGroup getGroup() {
    return group;
  }

  /** Verify compatibility based on whther or not the types belong to the same group
   * 
   * @param unitType to verify
   * @return true when in the same group, false otherwise
   */
  public boolean isCompatible(UnitType unitType) {
    return group.equals(unitType.group);
  }
}

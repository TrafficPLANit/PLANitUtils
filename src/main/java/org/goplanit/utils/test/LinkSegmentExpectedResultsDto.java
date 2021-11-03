package org.goplanit.utils.test;

/**
 * DTO object containing extra fields specific to BPR function
 * 
 * The first
 * 
 * @author gman6028
 *
 */
public class LinkSegmentExpectedResultsDto {

  /**
   * Link capacity
   */
  private double capacity;
  /**
   * Link length
   */
  private double length;
  /**
   * Link maximum speed
   */
  private double speed;

  /**
   * Id of start node
   */
  private long startNodeId;
  /**
   * Id of end node
   */
  private long endNodeId;
  /**
   * Link flow
   */
  private double linkFlow;
  /**
   * Calculated link cost
   */
  private double linkCost;
  /**
   * epsilon used for comparing doubles
   */
  private final static double epsilon = 0.0001;

  /**
   * Constructor
   * 
   * @param endNodeId id of end node
   * @param startNodeId id of start node
   * @param linkFlow flow through link
   * @param linkCost cost (travel time) of link
   * @param capacity capacity of the link (no lanes x capacity per lane)
   * @param length length of the link
   * @param speed travel speed of the link
   */
  public LinkSegmentExpectedResultsDto(long endNodeId, long startNodeId, double linkFlow, double linkCost,
      double capacity, double length, double speed) {
    this.startNodeId = startNodeId;
    this.endNodeId = endNodeId;
    this.linkFlow = linkFlow;
    this.linkCost = linkCost;
    this.capacity = capacity;
    this.length = length;
    this.speed = speed;
  }

  /**
   * Returns the capacity of this link
   * 
   * @return capacity of this link
   */
  public double getCapacity() {
    return capacity;
  }

  /**
   * Sets the capacity of this link
   * 
   * @param capacity the capacity of this link
   */
  public void setCapacity(double capacity) {
    this.capacity = capacity;
  }

  /**
   * Returns the length of this link
   * 
   * @return the length of this link
   */
  public double getLength() {
    return length;
  }

  /**
   * Set the length of this link
   * 
   * @param length the length of this link
   */
  public void setLength(double length) {
    this.length = length;
  }

  /**
   * Return the travel speed for this link
   * 
   * @return the travel speed for this link
   */
  public double getSpeed() {
    return speed;
  }

  /**
   * Set the travel speed for this link
   * 
   * @param speed the travel speed for this link
   */
  public void setSpeed(double speed) {
    this.speed = speed;
  }

  /**
   * Return the id of the start nonde
   * 
   * @return id of start node
   */
  public long getStartNodeId() {
    return startNodeId;
  }

  /**
   * Set the id of the end node
   * 
   * @param startNodeId id of the start node
   */
  public void setStartNodeId(long startNodeId) {
    this.startNodeId = startNodeId;
  }

  /**
   * Return the id of the end node
   * 
   * @return id of the end node
   */
  public long getEndNodeId() {
    return endNodeId;
  }

  /**
   * Set the id of the end node
   * 
   * @param endNodeId id of the end node
   */
  public void setEndNodeId(long endNodeId) {
    this.endNodeId = endNodeId;
  }

  /**
   * Return the flow through this link
   * 
   * @return the flow through this link
   */
  public double getLinkFlow() {
    return linkFlow;
  }

  /**
   * Set the flow through this link
   * 
   * @param linkFlow the flow through this link
   */
  public void setLinkFlow(double linkFlow) {
    this.linkFlow = linkFlow;
  }

  /**
   * Get the cost for the current link
   * 
   * @return the cost for the current link
   */
  public double getLinkCost() {
    return linkCost;
  }

  /**
   * Set the cost for the current link
   * 
   * @param linkCost the cost for the current link
   */
  public void setLinkCost(double linkCost) {
    this.linkCost = linkCost;
  }

  /**
   * Tests whether this ResultDto object is equal to another one
   * 
   * @param other ResultDto which is being compared to this one
   * @return true if all fields in the DTO objects are equal, false otherwise
   */

  public boolean equals(LinkSegmentExpectedResultsDto other) {
    if (startNodeId != other.getStartNodeId())
      return false;
    if (endNodeId != other.getEndNodeId())
      return false;
    if (Math.abs(linkCost - other.getLinkCost()) > epsilon)
      return false;
    if (Math.abs(linkFlow - other.getLinkFlow()) > epsilon)
      return false;
    return true;
  }

  /**
   * Return the hashCode for this object
   * 
   * @return hashCode for this object
   */
  public int hashCode() {
    double val = startNodeId + endNodeId * linkCost * linkFlow;
    return (int) Math.round(val);
  }

}

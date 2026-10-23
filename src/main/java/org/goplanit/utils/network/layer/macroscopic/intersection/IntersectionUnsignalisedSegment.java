package org.goplanit.utils.network.layer.macroscopic.intersection;

/**
 * A link segment of an intersection without a signal controlling its traffic: an approach of an unsignalised
 * intersection, an approach without a signal of its own at a signalised one, or a plain internal segment. It has no
 * green ratio, only a saturation flow factor
 *
 * @author markr
 */
public interface IntersectionUnsignalisedSegment extends IntersectionSegment {

  /**
   * An unsignalised segment never has a signal
   *
   * @return false
   */
  @Override
  public default boolean hasSignal() {
    return false;
  }
}

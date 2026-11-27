package org.goplanit.utils.network.layer.macroscopic;

import org.goplanit.utils.graph.directed.BannedTurnFactory;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.mode.Mode;

import java.util.Collection;

/**
 * Factory to create the banned turns of a macroscopic layer and register them on its container. A banned turn created
 * without modes is banned for every mode
 *
 * @author markr
 */
public interface MacroscopicBannedTurnFactory extends BannedTurnFactory {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicBannedTurn create(final EdgeSegment from, final EdgeSegment to);

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicBannedTurn registerNew(final EdgeSegment from, final EdgeSegment to);

  /**
   * Create a banned turn for the given modes only and register it
   *
   * @param from the from link segment
   * @param to the to link segment
   * @param explicitlyBannedModes the modes the turn is banned for; when empty it is banned for every mode
   * @return the created and registered banned turn
   */
  public abstract MacroscopicBannedTurn registerNew(
      final MacroscopicLinkSegment from, final MacroscopicLinkSegment to, final Collection<Mode> explicitlyBannedModes);
}

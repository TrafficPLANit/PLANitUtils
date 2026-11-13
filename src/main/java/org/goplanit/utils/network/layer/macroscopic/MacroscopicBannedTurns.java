package org.goplanit.utils.network.layer.macroscopic;

import java.util.function.BiConsumer;

import org.goplanit.utils.graph.directed.BannedTurn;
import org.goplanit.utils.graph.directed.BannedTurns;

/**
 * Container for the banned turns of a macroscopic layer. Each banned turn it holds is a {@link MacroscopicBannedTurn},
 * which names the modes it bans, and its factory creates them
 *
 * @author markr
 */
public interface MacroscopicBannedTurns extends BannedTurns {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicBannedTurnFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicBannedTurns shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicBannedTurns deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicBannedTurns deepCloneWithMapping(BiConsumer<BannedTurn, BannedTurn> mapper);
}

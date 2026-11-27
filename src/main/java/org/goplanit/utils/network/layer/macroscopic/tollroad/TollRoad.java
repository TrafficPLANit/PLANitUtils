package org.goplanit.utils.network.layer.macroscopic.tollroad;

import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ManagedId;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;
import org.goplanit.utils.network.layer.physical.LinkSegment;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * A toll road: link segments of a layer that are tolled together on the ground, and who pays on them
 * <p>
 * For example the M4 and M4 East, its carriageways and ramps that carry a toll, paid by private cars and goods
 * vehicles. A toll road holds no amounts, times, sections or gates: those belong to a network pricing, whose schemes
 * refer to toll roads, several of them priced as one where an operator does so. Toll roads are held by their layer, as
 * intersections are, so the layer keeps them in step when its links are broken or removed. A link segment is on at
 * most one toll road of its layer.
 * </p>
 *
 * @author markr
 */
public interface TollRoad extends ExternalIdAble, ManagedId, Serializable {

  /** id class for generating ids */
  public static final Class<TollRoad> TOLL_ROAD_ID_CLASS = TollRoad.class;

  /**
   * {@inheritDoc}
   */
  @Override
  public default Class<? extends TollRoad> getIdClass() {
    return TOLL_ROAD_ID_CLASS;
  }

  /**
   * The name of the toll road, such as "M4 and M4 East"
   *
   * @return the name, null when it has none
   */
  public abstract String getName();

  /**
   * Set the name of the toll road
   *
   * @param name to set, null for none
   */
  public abstract void setName(String name);

  /**
   * The link segments of the toll road, its tolled link segments, referenced, not owned: its layer owns them
   *
   * @return unmodifiable list of the link segments, in the order given
   */
  public abstract List<MacroscopicLinkSegment> getLinkSegments();

  /**
   * Whether a link segment is one of the toll road's.
   *
   * @param linkSegment to check
   * @return true when it is one of the toll road's link segments
   */
  public abstract boolean containsLinkSegment(LinkSegment linkSegment);

  /**
   * The modes that pay on the toll road, as given: named, exempted, or not given
   *
   * @return the priced mode selection
   */
  public abstract PricedModeSelection getPricedModeSelection();

  /**
   * The modes that pay on the toll road among the modes of its layer: those named, or those paying by default, see
   * {@link PricedModeSelection#isPricedByDefault(Mode)}, less those exempted. They say who pays, never who may use the
   * road, which its link segments' allowed modes say
   *
   * @param modes the modes to check
   * @return the modes that pay, in the order of the layer's modes
   */
  public abstract Set<Mode> findPricedModes(Collection<? extends Mode> modes);

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract TollRoad shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract TollRoad deepClone();
}

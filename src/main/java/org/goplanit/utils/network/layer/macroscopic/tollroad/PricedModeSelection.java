package org.goplanit.utils.network.layer.macroscopic.tollroad;

import org.goplanit.utils.containers.ListUtils;
import org.goplanit.utils.misc.CollectionUtils;
import org.goplanit.utils.mode.Mode;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The modes that pay, as given: by name, by exemption, or not given, never two of them at once
 * <p>
 * A toll road, a scheme, a price override, a cap and a minimum each say which modes pay in one of three ways, as the
 * XML gives it: {@code pricedmoderefs="heavygoods"} names them, {@code pricedexemptmoderefs="motorbike"} takes modes
 * out of those paying by default, and neither leaves the default. Holding one selection, made through {@link #named},
 * {@link #exempting} or {@link #unspecified}, means none of them can hold both, and a writer writes back what was
 * given. Which modes that comes to depends on who asks:
 * </p>
 * <pre>
 * var pricedModes = tollRoad.getPricedModeSelection()
 *     .findPricedModesIn(layer.getSupportedModes(), PricedModeSelection::isPricedByDefault);
 * </pre>
 * <p>
 * for a toll road, the layer's modes with the default rule of the toll roads; for a scheme on toll roads, the modes
 * its roads price, all of them by default; for an override, a cap or a minimum, the modes its schemes charge.
 * </p>
 *
 * @author markr
 */
public final class PricedModeSelection implements Serializable {

  /** generated UID */
  private static final long serialVersionUID = 1L;

  /** the one selection that gives no modes */
  private static final PricedModeSelection UNSPECIFIED = new PricedModeSelection(List.of(), List.of());

  /** the modes named, in the order given, empty unless the modes are given by name */
  private final List<Mode> pricedModes;

  /** the modes exempted, in the order given, empty unless the modes are given by exemption */
  private final List<Mode> exemptedModes;

  /**
   * Constructor
   *
   * @param pricedModes the modes named, empty unless the modes are given by name
   * @param exempted the modes exempted, empty unless the modes are given by exemption
   */
  private PricedModeSelection(List<Mode> pricedModes, List<Mode> exempted) {
    this.pricedModes = pricedModes;
    this.exemptedModes = exempted;
  }

  /**
   * Whether a mode pays by default where a selection names no modes and is resolved against the modes of a layer or a
   * network, as for a toll road or a mode scheme: a motorised mode whose use is not public transport, active or
   * emergency, so private cars, goods vehicles, taxis and ride share are assumed to pay, while buses, trains, bicycles,
   * pedestrians and ambulances do not. The rule sits on the selection, not on the mode, as being priced is no property
   * of a mode
   *
   * @param mode to check
   * @return true when it pays by default
   */
  public static boolean isPricedByDefault(Mode mode) {
    return mode.isMotorisedMode() && !mode.isPublicTransportMode() && !mode.isActiveMode() && !mode.isEmergencyMode();
  }

  /**
   * The modes that pay given by name
   *
   * @param modes named, at least one
   * @return the selection
   */
  public static PricedModeSelection named(Collection<? extends Mode> modes) {
    return new PricedModeSelection(CollectionUtils.copyToNonEmptyList(modes), List.of());
  }

  /**
   * The modes that pay given by exempting modes from those paying by default
   *
   * @param modes exempted, at least one
   * @return the selection
   */
  public static PricedModeSelection exempting(Collection<? extends Mode> modes) {
    return new PricedModeSelection(List.of(), CollectionUtils.copyToNonEmptyList(modes));
  }

  /**
   * The modes that pay not given, so those paying by default do
   *
   * @return the selection
   */
  public static PricedModeSelection unspecified() {
    return UNSPECIFIED;
  }

  /**
   * The modes named
   *
   * @return the modes named, in the order given, empty unless the modes are given by name
   */
  public List<Mode> getPricedModes() {
    return pricedModes;
  }

  /**
   * The modes exempted
   *
   * @return the modes exempted, in the order given, empty unless the modes are given by exemption
   */
  public List<Mode> getExemptedModes() {
    return exemptedModes;
  }

  /**
   * Whether the modes are not given, so those paying by default pay
   *
   * @return true when neither named nor exempted
   */
  public boolean isUnspecified() {
    return pricedModes.isEmpty() && exemptedModes.isEmpty();
  }

  /**
   * The modes that pay among the candidates: those named; or those paying by default less those exempted; or, not
   * given, those paying by default
   *
   * @param candidates the modes that could pay, such as a layer's modes or the modes a scheme's toll roads price
   * @param byDefault whether a candidate pays by default, such as {@link #isPricedByDefault(Mode)}
   * @return the modes that pay, in the order of the candidates
   */
  public Set<Mode> findPricedModesIn(Collection<? extends Mode> candidates, Predicate<? super Mode> byDefault) {
    Predicate<Mode> pays = !pricedModes.isEmpty() ? mode -> ListUtils.containsByIdentity(pricedModes, mode) :
        mode -> byDefault.test(mode) && !ListUtils.containsByIdentity(exemptedModes, mode);
    return candidates.stream().filter(pays).collect(Collectors.toCollection(LinkedHashSet::new));
  }

  /**
   * Whether a part of a scheme with this selection, such as a minimum or a cap, applies to a mode its scheme charges:
   * always when the modes are not given, when the mode is named, or when it is not exempted, see
   * {@link #findPricedModesIn} with every charged mode paying by default
   *
   * @param chargedMode a mode the scheme charges
   * @return true when the part applies to it
   */
  public boolean appliesTo(Mode chargedMode) {
    return !findPricedModesIn(List.of(chargedMode), mode -> true).isEmpty();
  }

  /**
   * The modes named or exempted that the candidates lack, so they can be dropped or reported, such as car named on a
   * toll road only heavy goods vehicles pay on
   *
   * @param candidates the modes that could pay
   * @return the modes given that are not among the candidates, in the order given, empty when the modes are not given
   */
  public Set<Mode> findPricedOrExemptedModesNotIn(Collection<? extends Mode> candidates) {
    var given = !pricedModes.isEmpty() ? pricedModes : exemptedModes;
    return given.stream().filter(mode -> !ListUtils.containsByIdentity(candidates, mode))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }
}

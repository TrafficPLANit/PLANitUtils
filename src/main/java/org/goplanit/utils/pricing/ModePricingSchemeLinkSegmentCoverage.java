package org.goplanit.utils.pricing;

import org.goplanit.utils.containers.ListUtils;
import org.goplanit.utils.network.layer.MacroscopicNetworkLayer;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegmentType;
import org.goplanit.utils.network.layers.MacroscopicNetworkLayers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * The link segments a mode scheme charges on, as the XML's {@code <include>} and {@code <exclude>} give them, tested on
 * demand
 * <p>
 * A link segment is covered when it lies in a layer considered (every layer when none is given), is of an
 * included type or is included itself (every link segment of those layers when neither is given), and is neither of an
 * excluded type nor excluded itself. For example a truck charge on motorways, trunk and primary roads but not their
 * ramps: types motorway, trunk and primary included, type motorway_link excluded. As types and layers are kept as
 * given rather than turned into a list of link segments, a charge over all motorways stays right when links are broken;
 * only what the network removes needs following, see {@link #removeLinkSegment} and {@link #removeLinkSegmentType}. A
 * coverage left with neither an included type nor an included link segment would hold every link segment of its
 * layers, which {@link #hasIncludedLinkSegmentTypesOrLinkSegments()} tells.
 * </p>
 *
 * @author markr
 */
public final class ModePricingSchemeLinkSegmentCoverage implements Serializable {

  /** generated UID */
  private static final long serialVersionUID = 1L;

  /**
   * Take an entry off the included and the excluded entries of its kind, found by identity
   *
   * @param <T> type of the entries
   * @param included entries of the kind
   * @param excluded entries of the kind
   * @param entry to take off
   * @return true when it was included or excluded
   */
  private static <T> boolean removeFromIncludedOrExcluded(List<T> included, List<T> excluded, T entry) {
    boolean removedIncluded = included.removeIf(held -> held == entry);
    boolean removedExcluded = excluded.removeIf(held -> held == entry);
    return removedIncluded || removedExcluded;
  }

  /** the layers considered, empty for every layer */
  private final List<MacroscopicNetworkLayer> consideredLayers = new ArrayList<>();

  /** the link segment types included */
  private final List<MacroscopicLinkSegmentType> includedLinkSegmentTypes = new ArrayList<>();

  /** the link segments included one by one */
  private final List<MacroscopicLinkSegment> includedLinkSegments = new ArrayList<>();

  /** the link segment types excluded */
  private final List<MacroscopicLinkSegmentType> excludedLinkSegmentTypes = new ArrayList<>();

  /** the link segments excluded one by one */
  private final List<MacroscopicLinkSegment> excludedLinkSegments = new ArrayList<>();

  /**
   * Whether a link segment lies in a layer considered, found through the layer's own link segments, as a link segment
   * does not know its layer; every layer counts when none is given
   *
   * @param linkSegment to check
   * @return true when it lies in a layer considered
   */
  private boolean liesInConsideredLayer(MacroscopicLinkSegment linkSegment) {
    return consideredLayers.isEmpty()
        || consideredLayers.stream().anyMatch(layer -> layer.getLinkSegments().get(linkSegment.getId()) == linkSegment);
  }

  /** Constructor of a coverage of every link segment of every layer, until something is included or excluded */
  public ModePricingSchemeLinkSegmentCoverage() {
  }

  /**
   * Consider layers, which limit where the included types and link segments count, the XML's
   * {@code layerrefs} of {@code <include>}
   *
   * @param layers to consider
   */
  public void considerLayers(Collection<? extends MacroscopicNetworkLayer> layers) {
    consideredLayers.addAll(layers);
  }

  /**
   * Include link segment types, the XML's {@code typerefs} of {@code <include>}
   *
   * @param linkSegmentTypes to include
   */
  public void includeLinkSegmentTypes(Collection<? extends MacroscopicLinkSegmentType> linkSegmentTypes) {
    includedLinkSegmentTypes.addAll(linkSegmentTypes);
  }

  /**
   * Include link segments one by one, the XML's {@code lsrefs} of {@code <include>}
   *
   * @param linkSegments to include
   */
  public void includeLinkSegments(Collection<? extends MacroscopicLinkSegment> linkSegments) {
    includedLinkSegments.addAll(linkSegments);
  }

  /**
   * Exclude link segment types, the XML's {@code typerefs} of {@code <exclude>}
   *
   * @param linkSegmentTypes to exclude
   */
  public void excludeLinkSegmentTypes(Collection<? extends MacroscopicLinkSegmentType> linkSegmentTypes) {
    excludedLinkSegmentTypes.addAll(linkSegmentTypes);
  }

  /**
   * Exclude link segments one by one, the XML's {@code lsrefs} of {@code <exclude>}
   *
   * @param linkSegments to exclude
   */
  public void excludeLinkSegments(Collection<? extends MacroscopicLinkSegment> linkSegments) {
    excludedLinkSegments.addAll(linkSegments);
  }

  /**
   * The layers considered
   *
   * @return unmodifiable list of the layers, empty for every layer
   */
  public List<MacroscopicNetworkLayer> getConsideredLayers() {
    return Collections.unmodifiableList(consideredLayers);
  }

  /**
   * The link segment types included
   *
   * @return unmodifiable list of the types
   */
  public List<MacroscopicLinkSegmentType> getIncludedLinkSegmentTypes() {
    return Collections.unmodifiableList(includedLinkSegmentTypes);
  }

  /**
   * The link segments included one by one
   *
   * @return unmodifiable list of the link segments
   */
  public List<MacroscopicLinkSegment> getIncludedLinkSegments() {
    return Collections.unmodifiableList(includedLinkSegments);
  }

  /**
   * The link segment types excluded
   *
   * @return unmodifiable list of the types
   */
  public List<MacroscopicLinkSegmentType> getExcludedLinkSegmentTypes() {
    return Collections.unmodifiableList(excludedLinkSegmentTypes);
  }

  /**
   * The link segments excluded one by one
   *
   * @return unmodifiable list of the link segments
   */
  public List<MacroscopicLinkSegment> getExcludedLinkSegments() {
    return Collections.unmodifiableList(excludedLinkSegments);
  }

  /**
   * Take a link segment the network removes off the link segments included or excluded one by one
   *
   * @param linkSegment removed from the network
   * @return true when it was included or excluded one by one
   */
  public boolean removeLinkSegment(MacroscopicLinkSegment linkSegment) {
    return removeFromIncludedOrExcluded(includedLinkSegments, excludedLinkSegments, linkSegment);
  }

  /**
   * Take a link segment type the network removes off the types included or excluded
   *
   * @param linkSegmentType removed from the network
   * @return true when it was included or excluded
   */
  public boolean removeLinkSegmentType(MacroscopicLinkSegmentType linkSegmentType) {
    return removeFromIncludedOrExcluded(includedLinkSegmentTypes, excludedLinkSegmentTypes, linkSegmentType);
  }

  /**
   * Whether a link segment type or a link segment is included; with neither, it covers every link segment of
   * the layers considered
   *
   * @return true when a type or a link segment is included
   */
  public boolean hasIncludedLinkSegmentTypesOrLinkSegments() {
    return !includedLinkSegmentTypes.isEmpty() || !includedLinkSegments.isEmpty();
  }

  /**
   * Whether a link segment is covered: in a layer considered, of an included type or included itself, or of
   * every type when neither is given, and neither of an excluded type nor excluded itself
   *
   * @param linkSegment to check
   * @return true when it is covered
   */
  public boolean contains(MacroscopicLinkSegment linkSegment) {
    var linkSegmentType = linkSegment.getLinkSegmentType();
    boolean included = !hasIncludedLinkSegmentTypesOrLinkSegments()
        || ListUtils.containsByIdentity(includedLinkSegmentTypes, linkSegmentType)
        || ListUtils.containsByIdentity(includedLinkSegments, linkSegment);
    boolean excluded = ListUtils.containsByIdentity(excludedLinkSegmentTypes, linkSegmentType)
        || ListUtils.containsByIdentity(excludedLinkSegments, linkSegment);
    return included && !excluded && liesInConsideredLayer(linkSegment);
  }

  /**
   * The link segments covered, from the layers considered, or from every layer of a network when none is given
   *
   * @param layers the layers of the network the coverage is of
   * @return the link segments covered, layer by layer
   */
  public Stream<MacroscopicLinkSegment> stream(MacroscopicNetworkLayers layers) {
    var layersToSearch = consideredLayers.isEmpty() ? layers.stream() : consideredLayers.stream();
    return layersToSearch.flatMap(layer -> layer.getLinkSegments().stream()).filter(this::contains);
  }
}

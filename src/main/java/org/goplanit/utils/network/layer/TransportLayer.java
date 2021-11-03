package org.goplanit.utils.network.layer;

import java.util.Collection;

import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ManagedId;
import org.goplanit.utils.mode.Mode;

/**
 * An infrastructure layer represents the infrastructure suited for a number of modes. This can be in the form of a physical network or by some other (more aggregate)
 * representation. The combination of infrastructure layers can be used to construct an intermodal network. Each layer supports one or more modes
 * 
 * @author markr
 *
 */
public interface TransportLayer extends ExternalIdAble, ManagedId {

  /** class used for managed id generation */
  public static final Class<TransportLayer> TRANSPORT_LAYER_ID_CLASS = TransportLayer.class;

  /**
   * {@inheritDoc}
   */
  @Override
  default Class<TransportLayer> getIdClass() {
    return TRANSPORT_LAYER_ID_CLASS;
  }
  
  /**
   * create a string that can be used to prefix log statements for this layer to - in a unified way - identify this statement came from a particular layer
   * 
   * @param layer to use
   * @return String "[layer: xmlID ]"
   */
  public static String createLayerLogPrefix(TransportLayer layer) {
    return String.format("[LAYER: %s ]", layer.getXmlId());
  }

  /**
   * register a mode as supported by this layer
   * 
   * @param supportedMode to support
   * @return true when successful false otherwise
   */
  public abstract boolean registerSupportedMode(Mode supportedMode);

  /**
   * register modes as supported by this layer
   * 
   * @param supportedModes to support
   * @return true when successful false otherwise
   */
  public abstract boolean registerSupportedModes(Collection<Mode> supportedModes);

  /**
   * collect the modes supported by this infrastructure layer
   * 
   * @return the supported modes for at least some part of the available infrastructure
   */
  public abstract Collection<Mode> getSupportedModes();

  /**
   * check if the layer is empty of any infrastructure
   * 
   * @return true when empty, false otherwise
   */
  public abstract boolean isEmpty();

  /**
   * invoked by entities inquiring about general information about the layer to display to users
   * 
   * @param prefix optional prefix to include in each line of logging
   */
  public abstract void logInfo(String prefix);

  /**
   * validate the infrastructure of this layer
   * 
   * @return true when valid, false otherwise
   */
  public abstract boolean validate();
  
  /**
   * Determine if mode is supported by this layer
   * 
   * @param mode to verify
   * @return true when supporting, false otherwise
   */
  public default boolean supports(Mode mode) {
    return getSupportedModes().contains(mode);
  }
  
  /**
   * Determine if layer contains any supported modes
   * 
   * @return true when supporting a mode, false otherwise
   */
  public default boolean hasSupportedModes() {
    return !getSupportedModes().isEmpty();
  }   
  
  /**
   * Collect the first mode found under the supported modes from the collection provided by getSupportedModes
   * 
   * @return first supported mode
   */
  public default Mode getFirstSupportedMode() {
    return hasSupportedModes() ? getSupportedModes().iterator().next() : null;
  }

  /**
   * Reset the layer
   */
  public abstract void reset();    

}

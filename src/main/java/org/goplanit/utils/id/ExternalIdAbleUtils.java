package org.goplanit.utils.id;

import org.goplanit.utils.misc.Pair;

/**
 * Id utils for ExternableId instances
 *
 * @author markr
 */
public class ExternalIdAbleUtils {

  /**
   * Based on a pair of XMLids construct a combined XML id. In case entry of pair is null, inject "N/A"
   *
   * @param xmlIdPair pair of instances that are ExternalIdAbles
   * @param separator to use
   * @param postFixToApply apply to each XmlId
   * @return combined XmlId string in the form of "pair_first+postfix | pair_second+postfix"
   */
  public static String joinXmlIdPair(
          Pair<? extends ExternalIdAble,? extends ExternalIdAble> xmlIdPair,
          String separator,
          String postFixToApply){

    String noXmlId = "N/A"; // apply in case no Xml id is set
    StringBuilder sb = new StringBuilder();
    sb.append(
            (xmlIdPair.firstNotNull() && xmlIdPair.first().hasXmlId()) ? xmlIdPair.first().getXmlId() : noXmlId).append(
                    postFixToApply);
    sb.append(separator); // separator
    sb.append(xmlIdPair.secondNotNull() && xmlIdPair.second().hasXmlId() ? xmlIdPair.second().getXmlId() : noXmlId).append(
            postFixToApply);
    return sb.toString();
  }
}

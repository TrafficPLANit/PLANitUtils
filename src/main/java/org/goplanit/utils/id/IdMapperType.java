package org.goplanit.utils.id;

import org.goplanit.utils.exceptions.PlanItRunTimeException;

import java.util.function.Function;

/**
 * Indicates which PLANit id to use for extracting ids on the output format.
 * <ul>
 * <li>ID: use the PLANit internal id</li>
 * <li>EXTERNAL_ID: use the planit external id</li>
 * <li>XML: use xml id </li>
 * </ul>
 * 
 * @author markr
 *
 */
public enum IdMapperType {

  XML, ID, EXTERNAL_ID

}
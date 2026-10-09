package org.goplanit.utils.args;

/**
 * Supported styles for argument parsing
 * 
 * @author markr
 *
 */
public enum ArgumentStyle {

	/** {@code "/<key/>=/-/:/<value/>"} */
	DEFAULT,
	/** "{@code --/<key/> /<value/>"} */
	DOUBLEHYPHEN;
}

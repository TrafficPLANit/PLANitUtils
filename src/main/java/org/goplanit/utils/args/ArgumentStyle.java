package org.goplanit.utils.args;

/**
 * Supported styles for argument parsing
 * 
 * @author markr
 *
 */
public enum ArgumentStyle {

	/** "/<key/>=/-/:/<value/>" */
	DEFAULT,
	/** "--/<key/> /<value/>" */
	DOUBLEHYPHEN;
}

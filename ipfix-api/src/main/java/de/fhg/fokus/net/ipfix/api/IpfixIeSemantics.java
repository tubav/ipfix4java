package de.fhg.fokus.net.ipfix.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 *
 */
public enum IpfixIeSemantics {
	DEFAULT(0), 
	QUANTITY(1), 
	TOTALCOUNTER(2), 
	DELTACOUNTER(3), 
	IDENTIFIER(4), 
	FLAGS(5),
	UNASSIGNED(6);

	private final int value;
	private static final Logger logger = LoggerFactory.getLogger(IpfixIeSemantics.class);

	IpfixIeSemantics(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	public static IpfixIeSemantics getFromModelName( String  name ){
		if( name==null){
			return DEFAULT;
		}
		try {
			return IpfixIeSemantics.valueOf(name.toUpperCase());
		}catch (Exception e) {
			logger.debug("No ie semantics  matched for \"{}\"",name);
			return IpfixIeSemantics.DEFAULT;
		}
	}

}

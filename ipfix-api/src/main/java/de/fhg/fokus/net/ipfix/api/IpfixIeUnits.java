package de.fhg.fokus.net.ipfix.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 *
 */
public enum IpfixIeUnits {
	NONE(0),
	BITS(1),    
	OCTETS(2),    
	PACKETS(3),    
	FLOWS(4),    
	SECONDS(5),    
	MILLISECONDS(6),    
	MICROSECONDS(7),
	NANOSECONDS(8),
	FOUR_OCTET_WORDS(9),
	MESSAGES(10),
	HOPS(11),   
	ENTRIES(12),
	UNASSIGNED(13);
	private final static Logger logger = LoggerFactory.getLogger(IpfixIeUnits.class);
	private final int value;
	IpfixIeUnits(int value){
		this.value = value;
	}
	public int getValue() {
		return value;
	}
	public static IpfixIeUnits getFromModelName( String  name ){
		if( name==null){
			return NONE;
		}
		try {
			String upper  = name.toUpperCase();
			if(upper.contentEquals("4-OCTET WORDS")){
				return FOUR_OCTET_WORDS;
			}
			return IpfixIeUnits.valueOf(name.toUpperCase());
		}catch (Exception e) {
			logger.debug("No units matched for \"{}\"",name);
			return IpfixIeUnits.NONE;
		}
	}


}

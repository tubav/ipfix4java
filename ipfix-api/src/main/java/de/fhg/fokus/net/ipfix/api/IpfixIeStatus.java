package de.fhg.fokus.net.ipfix.api;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 *
 */
public enum IpfixIeStatus {
	CURRENT("current"),
	DEPRECATED("deprecated"),
	OBSOLETE("obsolete");
	// TODO review: should we call it stringValue instead?
	private final String value;
	IpfixIeStatus( String value){
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	
}

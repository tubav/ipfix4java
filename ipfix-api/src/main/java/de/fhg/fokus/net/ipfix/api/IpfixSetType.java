package de.fhg.fokus.net.ipfix.api;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public enum IpfixSetType {
	INVALID(0,"invalidset"),
	TEMPLATE(2, "tset"),
	OPTIONS_TEMPLATE(3, "otset"),
	DATA(256, "dset");
	private final int value;
	private final String shortname;
	IpfixSetType(int value, String name) {
		this.value = value;
		this.shortname = name;
	}
	public int getValue() {
		return value;
	}
	public String getShortName() {
		return shortname;
	}
	/**
	 * Return respective set type.
	 * 
	 * @param setId
	 * @return
	 */
	public static IpfixSetType getSetType( int setId ){
		if( setId >= IpfixSetType.DATA.value){
			return IpfixSetType.DATA;
		}
		if( setId== IpfixSetType.TEMPLATE.value ){
			return IpfixSetType.TEMPLATE;
		} else if(setId == IpfixSetType.OPTIONS_TEMPLATE.value) {
			return IpfixSetType.OPTIONS_TEMPLATE;
		}
		return IpfixSetType.INVALID;
	}
}

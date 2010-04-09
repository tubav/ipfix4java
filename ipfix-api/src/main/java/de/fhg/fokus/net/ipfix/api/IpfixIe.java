package de.fhg.fokus.net.ipfix.api;
/**
 * 
 * @author FhG-FOKUS NETwork Research
 *
 */
public interface  IpfixIe {
	/**
	 * Value reserved for variable-length Information Elements.
	 */
	public int VARIABLE_LENGTH = 65535;
	public String getName();
	public IpfixIeDataTypes getDataType();
	public IpfixIeSemantics getSemantics();
	public IpfixIeStatus getStatus();
	public IpfixIeUnits getUnits();
	public IpfixFieldSpecifier getFieldSpecifier();
	
	/**
	 * Number of octets used for encoding this IPFIX IE.
	 * @return length or {@link IpfixIe#VARIABLE_LENGTH}
	 */
	public int getLength();

	public static class Util{
		public static String toString(IpfixIe ie) {
			return String.format("%s:%d", ie.getName(),ie.getLength());
		}
	}

}

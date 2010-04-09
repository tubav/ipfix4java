package de.fhg.fokus.net.ipfix.api;

/**
 * <p>
 * References
 * <ul>
 * <li><a href="http://www.iana.org/go/rfc5610">RFC 5102 Exporting Type
 * Information for IP Flow Information Export (IPFIX) Information Elements</a></li>
 * <li><a href="http://tools.ietf.org/html/rfc5102">RFC 5102 - Information Model
 * for IP Flow Information Export </a></li>
 * </ul>
 * </p>
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public enum IpfixIeDataTypes {
	/**
	 * The type "octetArray" represents a finite-length string of octets.
	 */
	OCTETARRAY(0, IpfixIe.VARIABLE_LENGTH),
	/**
	 * The type "unsigned8" represents a non-negative integer value in the range
	 * of 0 to 255.
	 */
	UNSIGNED8(1, 1),
	/**
	 * The type "unsigned16" represents a non-negative integer value in the
	 * range of 0 to 65535.
	 */
	UNSIGNED16(2, 2),
	/**
	 * The type "unsigned32" represents a non-negative integer value in the
	 * range of 0 to 4294967295.
	 */
	UNSIGNED32(3, 4),
	/**
	 * The type "unsigned64" represents a non-negative integer value in the
	 * range of 0 to 18446744073709551615.
	 */
	UNSIGNED64(4, 8),
	/**
	 * The type "signed8" represents an integer value in the range of -128 to
	 * 127.
	 */
	SIGNED8(5, 1),
	/**
	 * The type "signed16" represents an integer value in the range of -32768 to
	 * 32767.
	 */
	SIGNED16(6, 2),
	/**
	 * The type "signed32" represents an integer value in the range of
	 * -2147483648 to 2147483647.
	 */
	SIGNED32(7, 4),
	/**
	 * The type "signed64" represents an integer value in the range of
	 * -9223372036854775808 to 9223372036854775807.
	 */
	SIGNED64(8, 8),
	/**
	 * The type "float32" corresponds to an IEEE single-precision 32-bit
	 * floating point type as defined in [IEEE.754.1985].
	 */
	FLOAT32(9, 4),
	/**
	 * The type "float64" corresponds to an IEEE double-precision 64-bit
	 * floating point type as defined in [IEEE.754.1985].
	 */
	FLOAT64(10, 8),
	/**
	 * The type "boolean" represents a binary value. The only allowed values are
	 * "true" and "false".
	 */
	BOOLEAN(11, 1),
	/**
	 * The type "macAddress" represents a string of 6 octets.
	 */
	MACADDRESS(12, 6),
	/**
	 * The type "string" represents a finite-length string of valid characters
	 * from the Unicode character encoding set [ISO.10646- 1.1993]. Unicode
	 * allows for ASCII [ISO.646.1991] and many other international character
	 * sets to be used.
	 */
	STRING(13, IpfixIe.VARIABLE_LENGTH),
	/**
	 * The type "dateTimeSeconds" represents a time value in units of seconds
	 * based on coordinated universal time (UTC). The choice of an epoch, for
	 * example, 00:00 UTC, January 1, 1970, is left to corresponding encoding
	 * specifications for this type, for example, the IPFIX protocol
	 * specification. Leap seconds are excluded. Note that transformation of
	 * values might be required between different encodings if different epoch
	 * values are used.
	 */
	// TODO check this
	DATETIMESECONDS(14, IpfixIe.VARIABLE_LENGTH),
	/**
	 * The type "dateTimeMilliseconds" represents a time value in units of
	 * milliseconds based on coordinated universal time (UTC). The choice of an
	 * epoch, for example, 00:00 UTC, January 1, 1970, is left to corresponding
	 * encoding specifications for this type, for example, the IPFIX protocol
	 * specification. Leap seconds are excluded. Note that transformation of
	 * values might be required between different encodings if different epoch
	 * values are used.
	 */
	DATETIMEMILLISECONDS(15, IpfixIe.VARIABLE_LENGTH),
	/**
	 * The type "dateTimeMicroseconds" represents a time value in units of
	 * microseconds based on coordinated universal time (UTC). The choice of an
	 * epoch, for example, 00:00 UTC, January 1, 1970, is left to corresponding
	 * encoding specifications for this type, for example, the IPFIX protocol
	 * specification. Leap seconds are excluded. Note that transformation of
	 * values might be required between different encodings if different epoch
	 * values are used.
	 */
	DATETIMEMICROSECONDS(16, IpfixIe.VARIABLE_LENGTH),
	/**
	 * The type "dateTimeNanoseconds" represents a time value in units of
	 * nanoseconds based on coordinated universal time (UTC). The choice of an
	 * epoch, for example, 00:00 UTC, January 1, 1970, is left to corresponding
	 * encoding specifications for this type, for example, the IPFIX protocol
	 * specification. Leap seconds are excluded. Note that transformation of
	 * values might be required between different encodings if different epoch
	 * values are used.
	 */
	DATETIMENANOSECONDS(17, IpfixIe.VARIABLE_LENGTH),
	/**
	 * The type "ipv4Address" represents a value of an IPv4 address.
	 */
	IPV4ADDRESS(18, 4),
	/**
	 * The type "ipv6Address" represents a value of an IPv6 address.
	 */
	IPV6ADDRESS(19, 16);
	private final int encondingValue;
	private final int defaultLength;

	private IpfixIeDataTypes(int value, int length) {
		this.encondingValue = value;
		this.defaultLength = length;
	}

	/**
	 * Get encoding value.
	 * 
	 * @return
	 */
	public int getEncodingValue() {
		return encondingValue;
	}

	/**
	 * Get default length in octets.
	 * 
	 * @return Length in octets or {@link IpfixIe#VARIABLE_LENGTH}.
	 */
	public int getDefaultLength() {
		return defaultLength;
	}


}

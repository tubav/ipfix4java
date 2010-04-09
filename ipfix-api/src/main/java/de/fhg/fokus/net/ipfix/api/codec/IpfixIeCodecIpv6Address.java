package de.fhg.fokus.net.ipfix.api.codec;

import de.fhg.fokus.net.ipfix.api.IpfixIeCodec;
import de.fhg.fokus.net.ipfix.api.IpfixIeDataTypes;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public class IpfixIeCodecIpv6Address implements IpfixIeCodec {
	protected int fieldLength=getDataType().getDefaultLength();
	@Override
	public IpfixIeDataTypes getDataType() {
		return IpfixIeDataTypes.IPV6ADDRESS;
	}
}

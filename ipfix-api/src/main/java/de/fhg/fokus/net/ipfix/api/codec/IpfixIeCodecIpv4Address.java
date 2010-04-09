package de.fhg.fokus.net.ipfix.api.codec;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.fokus.net.ipfix.api.IpfixIeCodec;
import de.fhg.fokus.net.ipfix.api.IpfixIeDataTypes;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public class IpfixIeCodecIpv4Address implements IpfixIeCodec {
	// -- sys --
	private Logger logger = LoggerFactory.getLogger(getClass());
	// -- model --
	protected int fieldLength = getDataType().getDefaultLength();

	public Inet4Address getAddress(ByteBuffer setBuffer) {
		byte[] ba = new byte[4];
		setBuffer.get(ba);
		Inet4Address addr = null;
		try {
			addr = (Inet4Address) Inet4Address.getByAddress(ba);
		} catch (UnknownHostException e) {
			logger.warn("Invalid IPv4 address.", e.toString());
		}
		return addr;
	}

	@Override
	public IpfixIeDataTypes getDataType() {
		return IpfixIeDataTypes.IPV4ADDRESS;
	}
}

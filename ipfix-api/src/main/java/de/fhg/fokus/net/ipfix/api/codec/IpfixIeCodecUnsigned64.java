package de.fhg.fokus.net.ipfix.api.codec;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import de.fhg.fokus.net.ipfix.api.IpfixIeCodec;
import de.fhg.fokus.net.ipfix.api.IpfixIeDataTypes;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public abstract class IpfixIeCodecUnsigned64 implements IpfixIeCodec {
	/**
	 * You must set this.
	 */
	protected int fieldLength;

	@Override
	public IpfixIeDataTypes getDataType() {
		return IpfixIeDataTypes.UNSIGNED64;
	}
	public BigInteger getBigInteger(ByteBuffer setBuffer) {
		byte[] ba = new byte[fieldLength+1];
		ba[0]=0;
		setBuffer.get(ba,1,fieldLength);
		return new BigInteger(ba);
	}
}

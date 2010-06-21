package de.fhg.fokus.net.ipfix.api.codec;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.fokus.net.ipfix.api.IpfixIeCodec;
import de.fhg.fokus.net.ipfix.api.IpfixIeDataTypes;
import de.fhg.fokus.net.ipfix.util.ByteBufferUtil;

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
	// -- sys --
	private static final Logger logger = LoggerFactory
			.getLogger(IpfixIeCodecUnsigned64.class);

	@Override
	public IpfixIeDataTypes getDataType() {
		return IpfixIeDataTypes.UNSIGNED64;
	}
	public long getLong(ByteBuffer setBuffer) {
		if (fieldLength == 4) {
			return ByteBufferUtil.getUnsignedInt(setBuffer);
		} else if (fieldLength == 2) {
			return ByteBufferUtil.getUnsignedShort(setBuffer);
		} else if (fieldLength == 1) {
			return ByteBufferUtil.getUnsignedByte(setBuffer);
		}
		logger.warn("Invalid unsigned64 encoding, returning 0., length:{}",
				fieldLength);
		return 0;
	}
	
	public BigInteger getBigInteger(ByteBuffer setBuffer) {
		byte[] ba = new byte[fieldLength+1];
		ba[0]=0;
		setBuffer.get(ba,1,fieldLength);
		return new BigInteger(ba);
	}
}

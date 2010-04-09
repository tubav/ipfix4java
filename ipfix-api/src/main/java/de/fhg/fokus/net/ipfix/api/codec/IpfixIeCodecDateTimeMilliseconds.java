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
public class IpfixIeCodecDateTimeMilliseconds implements IpfixIeCodec {
	protected int fieldLength = getDataType().getDefaultLength();

	@Override
	public IpfixIeDataTypes getDataType() {
		return IpfixIeDataTypes.DATETIMEMICROSECONDS;
	}
	public BigInteger getBigInteger(ByteBuffer setBuffer){
		byte bb [] = new byte[fieldLength];
		setBuffer.get(bb);
		return new BigInteger(bb);
	}

}

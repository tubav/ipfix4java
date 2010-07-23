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
public class IpfixIeCodecDateTimeMicroseconds implements IpfixIeCodec {
	protected int fieldLength = getDataType().getDefaultLength();

	@Override
	public IpfixIeDataTypes getDataType() {
		return IpfixIeDataTypes.DATETIMEMILLISECONDS;
	}
	public BigInteger getBigInteger(ByteBuffer setBuffer) {
		// TODO add support to compressed encodings
		int fieldLength=8;
		byte[] ba = new byte[fieldLength+1];
		ba[0]=0;
		setBuffer.get(ba,1,fieldLength);
		return new BigInteger(ba);
	}

	
}

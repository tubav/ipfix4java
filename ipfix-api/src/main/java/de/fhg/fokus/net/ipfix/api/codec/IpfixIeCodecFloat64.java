package de.fhg.fokus.net.ipfix.api.codec;

import java.nio.ByteBuffer;

import de.fhg.fokus.net.ipfix.api.IpfixIeCodec;
import de.fhg.fokus.net.ipfix.api.IpfixIeDataTypes;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public class IpfixIeCodecFloat64 implements IpfixIeCodec {
	protected int fieldLength = getDataType().getDefaultLength();

	@Override
	public IpfixIeDataTypes getDataType() {
		return IpfixIeDataTypes.FLOAT64;
	}
	// TODO review 
	public double getDouble(ByteBuffer setBuffer) {
		return setBuffer.getDouble();
	}


}

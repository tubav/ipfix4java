package de.fhg.fokus.net.ipfix.api.codec;

import java.nio.ByteBuffer;

import de.fhg.fokus.net.ipfix.api.IpfixIeCodec;
import de.fhg.fokus.net.ipfix.api.IpfixIeDataTypes;
import de.fhg.fokus.net.ipfix.util.ByteBufferUtil;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public class IpfixIeCodecUnsigned8 implements IpfixIeCodec {
	protected int fieldLength = getDataType().getDefaultLength();

	@Override
	public IpfixIeDataTypes getDataType() {
		return IpfixIeDataTypes.UNSIGNED8;
	}

	public short getShort( ByteBuffer setBuffer){
	    return ByteBufferUtil.getUnsignedByte(setBuffer);
	}

}

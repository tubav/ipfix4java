package de.fhg.fokus.net.ipfix.api.codec;

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
public class IpfixIeCodecUnsigned32 implements IpfixIeCodec {
	// -- sys --
	private static final Logger logger = LoggerFactory
			.getLogger(IpfixIeCodecUnsigned32.class);
	protected int fieldLength = getDataType().getDefaultLength();

	@Override
	public IpfixIeDataTypes getDataType() {
		return IpfixIeDataTypes.UNSIGNED32;
	}

	public long getLong(ByteBuffer setBuffer) {
		if (fieldLength == 4) {
			return ByteBufferUtil.getUnsignedInt(setBuffer);
		} else if (fieldLength == 2) {
			return ByteBufferUtil.getUnsignedShort(setBuffer);
		} else if (fieldLength == 1) {
			return ByteBufferUtil.getUnsignedByte(setBuffer);
		}
		logger.warn("Invalid unsigned32 encoding, returning 0., length:{}",
				fieldLength);
		return 0;
	}

}

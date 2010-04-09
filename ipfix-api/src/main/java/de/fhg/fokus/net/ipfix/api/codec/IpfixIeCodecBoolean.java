package de.fhg.fokus.net.ipfix.api.codec;

import de.fhg.fokus.net.ipfix.api.IpfixIeCodec;
import de.fhg.fokus.net.ipfix.api.IpfixIeDataTypes;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public class IpfixIeCodecBoolean implements IpfixIeCodec {
	// private Logger logger = LoggerFactory.getLogger(getClass());
	protected int fieldLength = getDataType().getDefaultLength();

	@Override
	public IpfixIeDataTypes getDataType() {
		return IpfixIeDataTypes.BOOLEAN;
	}

}

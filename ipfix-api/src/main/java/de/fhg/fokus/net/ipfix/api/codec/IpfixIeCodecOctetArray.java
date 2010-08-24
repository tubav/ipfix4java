package de.fhg.fokus.net.ipfix.api.codec;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import de.fhg.fokus.net.ipfix.api.IpfixIeCodec;
import de.fhg.fokus.net.ipfix.api.IpfixIeDataTypes;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public class IpfixIeCodecOctetArray implements IpfixIeCodec {
	protected int fieldLength = getDataType().getDefaultLength();

	@Override
	public IpfixIeDataTypes getDataType() {
		return IpfixIeDataTypes.OCTETARRAY;
	}
	public ByteBuffer getByteBuffer( ByteBuffer setBuffer ){
		return VariableLength.getByteBuffer(setBuffer);
	}
	public String getString(ByteBuffer setBuffer, String charset ) throws UnsupportedEncodingException{
		ByteBuffer bbuf = VariableLength.getByteBuffer(setBuffer);
		byte[] bytes = new byte[bbuf.remaining()];
		bbuf.get(bytes);
		return new String(bytes,charset);
	}

}

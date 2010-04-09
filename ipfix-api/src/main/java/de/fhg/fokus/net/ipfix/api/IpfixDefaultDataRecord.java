package de.fhg.fokus.net.ipfix.api;

import java.nio.ByteBuffer;

import de.fhg.fokus.net.ipfix.util.HexDump;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 *
 */
public class IpfixDefaultDataRecord implements IpfixRecord {
	private final ByteBuffer byteBuffer;
	/**
	 * Slice the set buffer from the current position len bytes long.
	 * 
	 * @param setBuffer Origin byte buffer.
	 * @param len Size of record in octets.
	 */
	public IpfixDefaultDataRecord(ByteBuffer setBuffer, int len ) {
		this.byteBuffer = setBuffer.slice();
		this.byteBuffer.limit(len);
		setBuffer.position(setBuffer.position()+len );
	}
	@Override
	public String toString() {
		byte [] bb = new byte[byteBuffer.remaining()];
		byteBuffer.get(bb);
		String hexdump = HexDump.toHexString( bb, byteBuffer.limit()*2 );
		return String.format("drec:{ len:%d hex: %s }", byteBuffer.limit(),hexdump);
	}
	
}

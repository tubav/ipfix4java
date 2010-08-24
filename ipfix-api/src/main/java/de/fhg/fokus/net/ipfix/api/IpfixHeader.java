package de.fhg.fokus.net.ipfix.api;

import java.nio.ByteBuffer;

import de.fhg.fokus.net.ipfix.util.ByteBufferUtil;

/**
 * 
 * 
 * <pre>
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |       Version Number          |            Length             |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                           Export Time                         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                       Sequence Number                         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                    Observation Domain ID                      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 * 
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public class IpfixHeader {
	// private final static Logger logger =
	// LoggerFactory.getLogger(IpfixHeader.class);
	// -- constants --
	public static final int IDX_VERSION = 0;
	public static final int IDX_LENGTH = 2;
	public static final int IDX_EXPORT_TIME = 4;
	public static final int IDX_SEQUENCE_NUMBER = 8;
	public static final int IDX_OBSERVATION_DOMAIN_ID = 12;

	/**
	 * IPFIX header size in octets
	 */
	public static final int SIZE_IN_OCTETS = 16;

	public static  int getVersion(ByteBuffer byteBuffer ) {
		return ByteBufferUtil.getUnsignedShort(byteBuffer, IDX_VERSION);
	}
	/**
	 * Get IPFIX message length from position, buffer state won't be changed.
	 * 
	 * @param byteBuffer
	 * @param pos
	 * @return ipfix message length from reference message starting position
	 */
	public static int getLength(ByteBuffer byteBuffer, int pos ) {
		return ByteBufferUtil.getUnsignedShort(byteBuffer, pos+IDX_LENGTH);
	}

	public static int getLength(ByteBuffer byteBuffer ) {
		return ByteBufferUtil.getUnsignedShort(byteBuffer, IDX_LENGTH);
	}

	public static long getExportTime(ByteBuffer byteBuffer) {
		return ByteBufferUtil.getUnsignedInt(byteBuffer, IDX_EXPORT_TIME);

	}
	public static long getSequenceNumber(ByteBuffer byteBuffer) {
		return ByteBufferUtil.getUnsignedInt(byteBuffer, IDX_SEQUENCE_NUMBER);

	}
	public static long getObservationDomainID(ByteBuffer byteBuffer) {
		return ByteBufferUtil.getUnsignedInt(byteBuffer,
				IDX_OBSERVATION_DOMAIN_ID);
	}
	public static String toString( ByteBuffer byteBuffer ) {
		return String.format("hdr:{v:%d, len:%d, et:%d, sn:%d, oid:%d}",
				getVersion(byteBuffer), getLength(byteBuffer), getExportTime(byteBuffer),
				getSequenceNumber(byteBuffer), getObservationDomainID(byteBuffer));
	}

}

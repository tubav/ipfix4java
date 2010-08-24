package de.fhg.fokus.net.ipfix.api;

import java.nio.ByteBuffer;

import de.fhg.fokus.net.ipfix.util.ByteBufferUtil;

/**
 * 
 *         <pre>
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |          Set ID               |          Length               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 * 
 * @author FhG-FOKUS NETwork Research
 */
public class IpfixSetHeader {
	// -- sys --
//	private final static Logger logger = LoggerFactory
//	.getLogger(IpfixSetHeader.class);
	// -- constants --
	private static final int IDX_SETID = 0;
	private static final int IDX_LENGTH = 2;
	public static final int SIZE_IN_OCTETS = 4;
	// -- model --
	private int setId;
	private int length;

	/**
	 * Read an IPFIX set header from byte buffer. Byte buffer position is
	 * updated accordingly;
	 * 
	 * @param messageBuffer
	 */
	public IpfixSetHeader(ByteBuffer messageBuffer) {
		// slicing
		ByteBuffer bbuf = ByteBufferUtil.sliceAndSkip(messageBuffer, SIZE_IN_OCTETS);
//		messageBuffer.limit(messageBuffer.position() + SIZE_IN_OCTETS);
//		ByteBuffer bbuf = messageBuffer.slice();
//		messageBuffer.position(messageBuffer.limit()).limit(
//				messageBuffer.capacity());
		// reading
		this.setId = ByteBufferUtil.getUnsignedShort(bbuf, IDX_SETID);
		this.length = ByteBufferUtil.getUnsignedShort(bbuf, IDX_LENGTH);

	}
	/**
	 * <p>
	 * Set ID value identifies the Set. A value of 2 is reserved for the
	 * Template Set. A value of 3 is reserved for the Option Template Set. All
	 * other values from 4 to 255 are reserved for future use. Values above 255
	 * are used for Data Sets. The Set ID values of 0 and 1 are not used for
	 * historical reasons [RFC3954].
	 * </p>
	 * 
	 * @return Set id
	 */

	public int getSetId() {
		return setId;
	}

	public void setSetId(int setId) {
		this.setId = setId;
	}

	/**
	 * <p>
	 * Total length of the Set, in octets, including the Set Header, all
	 * records, and the optional padding. Because an individual Set MAY contain
	 * multiple records, the Length value MUST be used to determine the position
	 * of the next Set.
	 * </p>
	 * 
	 * @return total set length in octets
	 */
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public String toString() {
		return String.format("sethdr:{id:%d, len:%d}", setId, length);
	}
}

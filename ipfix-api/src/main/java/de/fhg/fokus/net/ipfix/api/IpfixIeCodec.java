package de.fhg.fokus.net.ipfix.api;

import java.nio.ByteBuffer;

import de.fhg.fokus.net.ipfix.util.ByteBufferUtil;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public interface IpfixIeCodec {
	public IpfixIeDataTypes getDataType();

	/**
	 * Deals with variable length Information Elements. There are two cases, as
	 * shown in the figures below. <h3>1. Length < 255 octets</h3>
	 * 
	 * <pre>
	 * 
	 *     0                   1                   2                   3
	 *     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *    | Length (< 255)|          Information Element                  |
	 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *    |                      ... continuing as needed                 |
	 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * 
	 *    Figure R: Variable-Length Information Element (length < 255 octets)
	 * 
	 * </pre>
	 * 
	 * <h3>2. Length > 255</h3>
	 * 
	 * <pre>
	 *    0                   1                   2                   3
	 *     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *    |      255      |      Length (0 to 65535)      |       IE      |
	 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *    |                      ... continuing as needed                 |
	 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * 
	 * </pre>
	 * 
	 * @author FhG-FOKUS NETwork Research
	 * 
	 */
	public static class VariableLength {
		// -- constants --
		public static final int IDX_SMALL_LENGTH = 0;
		public static final int IDX_IE_SMALL_LENGTH = 1;
		public static final int IDX_LENGTH = 1;
		public static final int IDX_IE_LENGTH = 3;
		public static final int IE_LENGTH_GREATER_THAN_255 = 255;

		/**
		 * Reads variable length information elements.
		 * 
		 * @param setBuffer
		 * @return
		 */
		public static ByteBuffer getByteBuffer(ByteBuffer setBuffer) {
			ByteBuffer byteBuffer = null;
			int length = ByteBufferUtil.getUnsignedByte(setBuffer,
					IDX_SMALL_LENGTH);
			int startPos = setBuffer.position();
			if (length == IE_LENGTH_GREATER_THAN_255) {
				length = ByteBufferUtil.getUnsignedShort(setBuffer,
						IDX_IE_LENGTH);
				startPos += IDX_IE_LENGTH;

			} else {
				startPos += IDX_IE_SMALL_LENGTH;

			}
			final int endPos = startPos + length;
			// slicing
			final int limit = setBuffer.limit();
			setBuffer.position(startPos).limit(endPos);
			byteBuffer = setBuffer.slice();
			// "consuming" bytes
			setBuffer.position(endPos);
			setBuffer.limit(limit);
			return byteBuffer;

		}

	}

}

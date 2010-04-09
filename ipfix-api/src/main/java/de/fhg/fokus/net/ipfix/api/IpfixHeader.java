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

	// -- model --
	private final ByteBuffer byteBuffer;
	/**
	 * IPFIX header size in octets
	 */
	public static final int SIZE_IN_OCTETS = 16;

	public IpfixHeader(ByteBuffer fileBuffer) {

		this.byteBuffer = ByteBufferUtil.sliceAndSkip(fileBuffer,
				SIZE_IN_OCTETS);

	}

	public int getVersion() {
		return ByteBufferUtil.getUnsignedShort(byteBuffer, IDX_VERSION);
	}

	public IpfixHeader setVersion(Integer version) {
		ByteBufferUtil.putUnsignedShort(byteBuffer, IDX_VERSION, version);
		return this;
	}

	public int getLength() {
		return ByteBufferUtil.getUnsignedShort(byteBuffer, IDX_LENGTH);
	}

	public IpfixHeader setLength(Integer length) {
		ByteBufferUtil.putUnsignedShort(byteBuffer, IDX_LENGTH, length);
		return this;
	}

	public Long getExportTime() {
		return ByteBufferUtil.getUnsignedInt(byteBuffer, IDX_EXPORT_TIME);

	}

	public IpfixHeader setExportTime(Long exportTime) {
		ByteBufferUtil.putUnsignedInt(byteBuffer, IDX_EXPORT_TIME, exportTime);
		return this;
	}

	public long getSequenceNumber() {
		return ByteBufferUtil.getUnsignedInt(byteBuffer, IDX_SEQUENCE_NUMBER);

	}

	public IpfixHeader setSequenceNumber(Long sequenceNumber) {
		ByteBufferUtil.putUnsignedInt(byteBuffer, IDX_SEQUENCE_NUMBER,
				sequenceNumber);
		return this;
	}

	public long getObservationDomainID() {
		return ByteBufferUtil.getUnsignedInt(byteBuffer,
				IDX_OBSERVATION_DOMAIN_ID);
	}

	public IpfixHeader setObservationDomainID(Long observationDomainID) {
		ByteBufferUtil.putUnsignedInt(byteBuffer, IDX_OBSERVATION_DOMAIN_ID,
				observationDomainID);
		return this;
	}

	@Override
	public String toString() {
		return String.format("hdr:{v:%d, len:%d, et:%d, sn:%d, oid:%d}",
				getVersion(), getLength(), getExportTime(),
				getSequenceNumber(), getObservationDomainID());
	}

}

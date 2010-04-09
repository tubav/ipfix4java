 package de.fhg.fokus.net.ipfix.api;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.fokus.net.ipfix.util.ByteBufferUtil;

/**
 * <pre>
 * 
 * 0                   1                   2                   3
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |E|  Information Element ident. |        Field Length           |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                      Enterprise Number                        |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * 
 * </pre>
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public class IpfixFieldSpecifier implements Comparable<IpfixFieldSpecifier> {
	// -- sys --
	private final static Logger logger = LoggerFactory
			.getLogger(IpfixFieldSpecifier.class);
	// -- constants --
	private static final int IDX_E_ID = 0;
	private static final int IDX_FIELD_LENGTH = 2;
	private static final int IDX_ENTERPRISE_NUMBER = 4;
	private static final int LENGTH_IN_OCTETS = 4;
	private static final int LENGTH_ENTERPRISE_IN_OCTETS = 8;
	private static final int RFC_NON_ENTERPRISE=0;
	// -- model --
	private final ByteBuffer bytebuffer;
	private IpfixIe informationElement;
	private boolean isScope = false;

	@Override
	public String toString() {
		if (informationElement != null) {
			return String.format("%s", informationElement.getName());
		}
		return getUid();
	}

	public String getUid() {
		String scope = this.isScope?", scope:true":"";
		return String.format("{id:%d, len:%d, en:%d%s}", getId(),
				getIeLength(), getEnterpriseNumber(), scope);
	}
	/**
	 * Interpret next bytes as an ipfix field specifier. The record buffer position
	 * is incremented accordingly.
	 * 
	 * @param recordBuffer
	 */
	public IpfixFieldSpecifier(ByteBuffer recordBuffer) {
		this.bytebuffer = recordBuffer.slice();
		if (isEnterprise()) {
			recordBuffer.position(recordBuffer.position()
					+ LENGTH_ENTERPRISE_IN_OCTETS);
			this.bytebuffer.limit(LENGTH_ENTERPRISE_IN_OCTETS);
		} else {
			recordBuffer.position(recordBuffer.position() + LENGTH_IN_OCTETS);
			this.bytebuffer.limit(LENGTH_IN_OCTETS);
		}
	}
	/**
	 * Creates an IETF information element.
	 */
	public IpfixFieldSpecifier() {
		this(RFC_NON_ENTERPRISE);
	}
	/**
	 * Creates an IETF or Enterprise information element.
	 * 
	 * @param isEnterprise
	 */
	public IpfixFieldSpecifier(long enterpriseNumber) {
		
		if (enterpriseNumber!=0) {
			this.bytebuffer = ByteBuffer.allocate(LENGTH_ENTERPRISE_IN_OCTETS);
			setEnterpriseNumber(enterpriseNumber);
		} else {
			this.bytebuffer = ByteBuffer.allocate(LENGTH_IN_OCTETS);
		}
	}

	public short getId() {
		return (short) (0x7FFF & bytebuffer.getShort(IDX_E_ID));
	}

	public IpfixFieldSpecifier setId(int id) {
		bytebuffer.putShort(IDX_E_ID,
				(short) (id | (bytebuffer.getShort(IDX_E_ID) & 0x8000)));
		return this;
	}

	public long getEnterpriseNumber() {
		if (isEnterprise() && bytebuffer.limit() >= LENGTH_ENTERPRISE_IN_OCTETS) {
			return ByteBufferUtil.getUnsignedInt(bytebuffer,
					IDX_ENTERPRISE_NUMBER);
		} else {
			return 0;
		}
	}

	/**
	 * Set enterprise number. Enterprise bit is also set if en != 0.
	 * 
	 * @param en
	 * @return itself
	 */
	public IpfixFieldSpecifier setEnterpriseNumber(long en) {
		if (bytebuffer.limit() >= LENGTH_ENTERPRISE_IN_OCTETS) {
			ByteBufferUtil.putUnsignedInt(bytebuffer, IDX_ENTERPRISE_NUMBER , en);
			setEnterprise(en != 0);
		} else {
			logger
					.warn("Trying to set enterprise number on a too small field specifier buffer.");
		}
		return this;
	}

	/**
	 * <p> The length of the corresponding encoded Information Element, in octets.
	 * Refer to [RFC5102]. The field length may be smaller than the definition
	 * in [RFC5102] if the reduced size encoding is used (see Section 6.2). The
	 * value 65535 is reserved for variable- length Information Elements (see
	 * Section 7).</p>
	 * 
	 * @return length in octets
	 */
	public int getIeLength() {
		return ByteBufferUtil.getUnsignedShort(bytebuffer, IDX_FIELD_LENGTH);
	}

	/**
	 * 
	 * @return Length in octets required to store this field specifier, i.e. 4
	 *         (standard) or 8 (enterprise) octets.
	 */
	public int getFieldSpecifierLength() {
		return isEnterprise() ? LENGTH_ENTERPRISE_IN_OCTETS : LENGTH_IN_OCTETS;
	}

	public IpfixFieldSpecifier setFieldLength(int fieldLength) {
		ByteBufferUtil
				.putUnsignedShort(bytebuffer, IDX_FIELD_LENGTH, fieldLength);
		return this;
	}

	public boolean isEnterprise() {
		return (bytebuffer.getShort(IDX_E_ID) & 0x8000) > 0;
	}

	public IpfixFieldSpecifier setEnterprise(boolean isEnterprise) {
		if (isEnterprise) {
			bytebuffer.putShort(IDX_E_ID, (short) (bytebuffer.getShort(IDX_E_ID) | 0x8000));
		} else {
			bytebuffer.putShort(IDX_E_ID, (short) (bytebuffer.getShort(IDX_E_ID) & 0x7FFF));
		}
		return this;
	}

	/**
	 * Get underlying byte buffer
	 * 
	 * @return
	 */
	public ByteBuffer getBytebuffer() {
		return bytebuffer;
	}

	@Override
	public int hashCode() {
		return bytebuffer.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IpfixFieldSpecifier) {
			IpfixFieldSpecifier that = (IpfixFieldSpecifier) obj;
			return bytebuffer.equals(that.getBytebuffer());
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(IpfixFieldSpecifier that) {
		return bytebuffer.compareTo(that.getBytebuffer());
	}

	public IpfixIe getInformationElement() {
		return informationElement;
	}

	public void setInformationElement(IpfixIe informationElement) {
		this.informationElement = informationElement;
	}

	public boolean isScope() {
		return isScope;
	}

	public IpfixFieldSpecifier setScope(boolean isScope) {
		this.isScope = isScope;
		return this;
	}


}

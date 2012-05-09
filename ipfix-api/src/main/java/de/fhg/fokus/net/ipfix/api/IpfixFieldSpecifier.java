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
	@SuppressWarnings("unused")
	private final static Logger logger = LoggerFactory.getLogger(IpfixFieldSpecifier.class);
	
	// -- constants --
	private static final int IDX_E_ID = 0;
	private static final int IDX_FIELD_LENGTH = 2;
	private static final int IDX_ENTERPRISE_NUMBER = 4;
	private static final int LENGTH_IN_OCTETS = 4;
	private static final int LENGTH_ENTERPRISE_IN_OCTETS = 8;
	private static final int RFC_NON_ENTERPRISE=0;

	// -- model --
	private ByteBuffer bytebuffer;
	private IpfixIe informationElement;
	private boolean isScope = false;
	
	private short id = 0;
	private int   ieLength = 0;
	private long  enterpriseNumber = 0;

	private boolean isEnterprise = false;

	private boolean hasChanged = false;

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
		setEnterpriseNumber(enterpriseNumber);
		// reserve at least bytes 
		this.bytebuffer = null;
		this.hasChanged = true;
	}

	/**
	 * Interpret next bytes as an ipfix field specifier. The record buffer position
	 * is incremented accordingly.
	 * 
	 * @param recordBuffer
	 */
	public IpfixFieldSpecifier(ByteBuffer recordBuffer) {
		this.bytebuffer = recordBuffer.slice();

		short en_id = bytebuffer.getShort(IDX_E_ID);
		setEnterprise((en_id & 0x8000) != 0);
		setId(en_id & 0x7FFF);
		setFieldLength(ByteBufferUtil.getUnsignedShort(bytebuffer, IDX_FIELD_LENGTH));
		
		if( isEnterprise() ) {
			setEnterpriseNumber(ByteBufferUtil.getUnsignedInt(bytebuffer, IDX_ENTERPRISE_NUMBER));
			
			this.bytebuffer.limit(LENGTH_ENTERPRISE_IN_OCTETS);
			recordBuffer.position(recordBuffer.position() + LENGTH_ENTERPRISE_IN_OCTETS);
		}
		else {
			this.bytebuffer.limit(LENGTH_IN_OCTETS);
			recordBuffer.position(recordBuffer.position() + LENGTH_IN_OCTETS);
		}
	}

	public short getId() {
		return (short) this.id;
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
		return this.ieLength;
	}

	public long getEnterpriseNumber() {
		return this.enterpriseNumber;
	}

	/**
	 * 
	 * @return Length in octets required to store this field specifier, i.e. 4
	 *         (standard) or 8 (enterprise) octets.
	 */
	public int getFieldSpecifierLength() {
		return isEnterprise() ? LENGTH_ENTERPRISE_IN_OCTETS : LENGTH_IN_OCTETS;
	}

	public String getUid() {
		String scope = this.isScope?", scope:true":"";
		return String.format("{id:%d, len:%d, en:%d%s}"
							, getId()
							, getIeLength()
							, getEnterpriseNumber()
							, scope
							);
	}

	@Override
	public String toString() {
		if (informationElement != null) {
			return String.format("%s", informationElement.getName());
		}
		return getUid();
	}

	public IpfixFieldSpecifier setId(int id) {
		this.id = (short) id;
		this.hasChanged = true;
		return this;
	}

	/**
	 * Set enterprise number. Enterprise bit is also set if en != 0.
	 * 
	 * @param en
	 * @return itself
	 */
	public IpfixFieldSpecifier setEnterpriseNumber(long en) {
		this.enterpriseNumber = en;
		setEnterprise(0 != en);
		this.hasChanged = true;
		return this;
	}

	public IpfixFieldSpecifier setEnterprise(boolean isEnterprise) {
		this.isEnterprise = isEnterprise;
		this.hasChanged = true;
		return this;
	}

	public IpfixFieldSpecifier setFieldLength(int fieldLength) {
		this.ieLength = fieldLength;
		this.hasChanged = true;
		return this;
	}

	public boolean isEnterprise() {
		return this.isEnterprise;
	}

	/**
	 * Get underlying byte buffer
	 * 
	 * @return
	 */
	public ByteBuffer getBytebuffer() {
		if( null == this.bytebuffer || hasChanged) {
			if( isEnterprise() ) {
				this.bytebuffer = ByteBuffer.allocate(LENGTH_ENTERPRISE_IN_OCTETS);
				ByteBufferUtil.putUnsignedInt( bytebuffer
											, IDX_ENTERPRISE_NUMBER 
											, this.enterpriseNumber);
			}
			else {
				this.bytebuffer = ByteBuffer.allocate(LENGTH_IN_OCTETS);
			}
			if( isEnterprise() ) {
				bytebuffer.putShort(IDX_E_ID, (short) (this.id | 0x8000));
			} 
			else {
				bytebuffer.putShort(IDX_E_ID, (short) (this.id & 0x7FFF));
			}
			ByteBufferUtil.putUnsignedShort(bytebuffer, IDX_FIELD_LENGTH, this.ieLength);
		}
		//logger.debug("{} {}", bytebuffer.array(), bytebuffer.toString());
		return bytebuffer;
	}

	@Override
	public int hashCode() {
		this.getBytebuffer().rewind();
		return this.getBytebuffer().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IpfixFieldSpecifier) {
			IpfixFieldSpecifier that = (IpfixFieldSpecifier) obj;
			// TODO: compare elements
			//logger.debug("{} == {}", getBytebuffer().array(), that.getBytebuffer().array());
			return getBytebuffer().equals(that.getBytebuffer());
		} 
		else {
			return false;
		}
	}

	@Override
	public int compareTo(IpfixFieldSpecifier that) {
		// TODO: compare elements
		return getBytebuffer().compareTo(that.getBytebuffer());
	}

	public IpfixIe getInformationElement() {
		return this.informationElement;
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

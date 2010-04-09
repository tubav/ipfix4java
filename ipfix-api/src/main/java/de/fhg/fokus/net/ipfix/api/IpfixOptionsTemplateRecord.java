package de.fhg.fokus.net.ipfix.api;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import de.fhg.fokus.net.ipfix.util.ByteBufferUtil;

/**
 * IPFIX Options Template Record.
 * 
 * <h2>Header</h2>
 * <pre>
 * 
 *  0                  1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |         Template ID (> 255)   |         Field Count           |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      Scope Field Count        |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 * 
 * <h2>Field Specifier </h2>
 * <pre>
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |E|  Information Element ident. |        Field Length           |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                      Enterprise Number                        |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 * <h2> Record Format </h2>
 * <pre>
 * +--------------------------------------------------+
 * | Options Template Record Header                   |
 * +--------------------------------------------------+
 * | Field Specifier                                  |
 * +--------------------------------------------------+
 * | Field Specifier                                  |
 * +--------------------------------------------------+
 *  ...
 * +--------------------------------------------------+
 * | Field Specifier                                  |
 * +--------------------------------------------------+
 * 
 * </pre>
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public class IpfixOptionsTemplateRecord implements IpfixDataRecordSpecifier {
	// -- constants --
	private static final int IDX_TEMPLATE_ID = 0;
	private static final int IDX_FIELD_COUNT = 2;
	private static final int IDX_SCOPE_FIELD_COUNT = 4;
	private static final int HEADER_SIZE_IN_OCTETS = 6;
	// -- model --
	private final ByteBuffer byteBuffer;
	private final List<IpfixFieldSpecifier> fieldSpecifiers;
	private final String uid;
	private final int dataRecordLength;
	private boolean isVariableLength = false;
	/**
	 * Reads an IPFIX options template format. 
	 * 
	 * @param templateManager
	 * @param setBuffer
	 */
	public IpfixOptionsTemplateRecord(IpfixTemplateManager templateManager,
			ByteBuffer setBuffer) {
		// We need to linearly read the templates so the limit of sliced buffer
		// will encompasses at first the whole set. This is fixed at the end.
		this.byteBuffer = setBuffer.slice();
		this.byteBuffer.position(HEADER_SIZE_IN_OCTETS);
		int fieldCount = getFieldCount();
		// TODO handle errors (check this value)
		int scopeThreshold = fieldCount - getScopeFieldCount(); 
		this.fieldSpecifiers = new ArrayList<IpfixFieldSpecifier>();
		StringBuffer uidSbuf = new StringBuffer();
		int dataRecordLength = 0;
		for( int i=0; i < fieldCount; i++ ){
			IpfixFieldSpecifier fs = new IpfixFieldSpecifier(byteBuffer);
			if( i >= scopeThreshold  ){
				fs.setScope(true);
			}
			int ieLength = fs.getIeLength();
			if (ieLength == IpfixIe.VARIABLE_LENGTH) {
				this.isVariableLength = true;
			}
			uidSbuf.append(fs.getUid());
			dataRecordLength+=ieLength;
			this.fieldSpecifiers.add(fs);
		}
		this.dataRecordLength = dataRecordLength;
		this.uid = uidSbuf.toString();
		int limit = this.byteBuffer.position();
		this.byteBuffer.limit(limit);
		setBuffer.position(setBuffer.position()+limit);

		// TODO handle errors
		templateManager.registerOptionsTemplateRecord(this);

	}
	@Override
	public int getTemplateId() {
		return ByteBufferUtil.getUnsignedShort(byteBuffer, IDX_TEMPLATE_ID);
	}

	public int getFieldCount() {
		return ByteBufferUtil.getUnsignedShort(byteBuffer, IDX_FIELD_COUNT);
	}

	public int getScopeFieldCount() {
		return ByteBufferUtil.getUnsignedShort(byteBuffer,
				IDX_SCOPE_FIELD_COUNT);
	}
	@Override
	public String toString() {
		return String.format("otrec:{tid:%d, fcnt:%d, sfcnt:%d, len:%d, dlen:%d, flds:%s }", 
				getTemplateId(), getFieldCount(), getScopeFieldCount(),
				byteBuffer.limit(), isVariableLength ? IpfixIe.VARIABLE_LENGTH:dataRecordLength,
						fieldSpecifiers );
	}
	/**
	 * 
	 * @return Length in octets of respective data record.
	 */
	@Override
	public int getDataRecordLength() {
		return dataRecordLength;
	}
	@Override
	public boolean isVariableLength() {
		return isVariableLength;
	}
	public String getUid() {
		return uid;
	}
}

package de.fhg.fokus.net.ipfix.api;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import de.fhg.fokus.net.ipfix.util.ByteBufferUtil;

/**
 * Template Record Header
 * 
 * <pre>
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      Template ID (> 255)      |         Field Count           |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * 
 * </pre>
 * 
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public final class IpfixTemplateRecord implements IpfixDataRecordSpecifier {
	// -- sys --
	// private final static Logger logger =
	// LoggerFactory.getLogger(IpfixTemplateRecord.class);
	// -- constants --
	private static final int IDX_TEMPLATE_ID = 0;
	private static final int IDX_FIELD_COUNT = 2;
	private static final int HEADER_SIZE_IN_OCTETS = 4;
	// -- model --
	private final List<IpfixFieldSpecifier> fieldSpecifiers;
	private final String uid;
	private final ByteBuffer byteBuffer;
	private final int dataRecordLength;
	private boolean isVariableLength = false;

	private int templateId;
	private int fieldCount;
	
	public IpfixTemplateRecord(IpfixTemplateManager templateManager,
			ByteBuffer setBuffer) {
		// We need to linearly read the templates so the limit of sliced buffer
		// will encompasses at first the whole set. This will be fixed at the
		// end.
		this.byteBuffer = setBuffer.slice();
		this.byteBuffer.position(HEADER_SIZE_IN_OCTETS);

		// decode record header
		this.templateId = decodeTemplateId();
		this.fieldCount = decodeFieldCount();

		this.fieldSpecifiers = new ArrayList<IpfixFieldSpecifier>();
		StringBuffer uidSbuf = new StringBuffer();
		int dataRecordLength = 0;
		for (int i = 0; i < fieldCount; i++) {
			IpfixFieldSpecifier fs = new IpfixFieldSpecifier(this.byteBuffer);
			IpfixIe ie = templateManager.getInformationElement(fs);
			if (ie != null) {
				fs.setInformationElement(ie);
			}
			this.fieldSpecifiers.add(fs);
			int ieLength = fs.getIeLength();
			if (ieLength == IpfixIe.VARIABLE_LENGTH) {
				this.isVariableLength = true;
			}
			dataRecordLength += ieLength;
			uidSbuf.append(fs.getUid());
		}
		int limit = this.byteBuffer.position();
		this.byteBuffer.limit(limit);
		setBuffer.position(setBuffer.position() + limit);
		this.dataRecordLength = dataRecordLength;
		this.uid = uidSbuf.toString();
		if(templateManager!=null){
			templateManager.registerTemplateRecord(this);
		}
	}

	public IpfixTemplateRecord(ByteBuffer setBuffer) {
		// We need to linearly read the templates so the limit of sliced buffer
		// will encompasses at first the whole set. 
		// This will be fixed at the end.
		this.byteBuffer = setBuffer.slice();
		// skip set header
		this.byteBuffer.position(HEADER_SIZE_IN_OCTETS);
		
		// decode record header
		this.templateId = decodeTemplateId();
		this.fieldCount = decodeFieldCount();
		
		// create a new List to store the ie's
		this.fieldSpecifiers = new ArrayList<IpfixFieldSpecifier>(fieldCount);
		// StringBuffer to create uid string
		StringBuffer uidSbuf = new StringBuffer();
		
		// start decoding field specifier
		int dataRecordLength = 0;
		for (int i = 0; i < fieldCount; i++) {
			IpfixFieldSpecifier fs = new IpfixFieldSpecifier(this.byteBuffer);
			this.fieldSpecifiers.add(fs);

			// set data record length only for fixed length ie's
			int ieLength = fs.getIeLength();
			if (ieLength == IpfixIe.VARIABLE_LENGTH) {
				this.isVariableLength = true;
			}
			else {
				dataRecordLength += ieLength;
			}
			uidSbuf.append(fs.getUid());
		}
		int limit = this.byteBuffer.position();
		this.byteBuffer.limit(limit);
		// set the setBuffer position to the end of this record
		setBuffer.position(setBuffer.position() + limit);
		this.dataRecordLength = dataRecordLength;
		this.uid = uidSbuf.toString();
	}

	@Override
	public String toString() {
		return String.format(
				"trec:{tid:%d, fcnt:%d, len=%d, dlen=%s, flds:%s}",
				getTemplateId(), 
				getFieldCount(), 
				byteBuffer.limit(),
				isVariableLength ? IpfixIe.VARIABLE_LENGTH : dataRecordLength, 
				fieldSpecifiers
				);
	}

	public List<IpfixFieldSpecifier> getFieldSpecifiers() {
		return fieldSpecifiers;
	}

	public String getUid() {
		return uid;
	}

	@Override
	public int getTemplateId() {
		return this.templateId;
	}

	public int getFieldCount() {
		return this.fieldCount;
	}

	private int decodeTemplateId() {
		return ByteBufferUtil.getUnsignedShort(byteBuffer, IDX_TEMPLATE_ID);
	}

	private int decodeFieldCount() {
		return ByteBufferUtil.getUnsignedShort(byteBuffer, IDX_FIELD_COUNT);
	}

	/**
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
}

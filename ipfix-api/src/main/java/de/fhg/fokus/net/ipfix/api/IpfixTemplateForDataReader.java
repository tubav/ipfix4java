package de.fhg.fokus.net.ipfix.api;

import java.nio.ByteBuffer;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 * FIXME rename-me please
 * 
 */
public class IpfixTemplateForDataReader {
	// -- model --
	private final ByteBuffer bbuf;
	private final IpfixIe [] ies;
	private final String uid;
	private final int recordSize; // used for slicing record buffers
	/**
	 *
	 * @param ies 
	 */
	public IpfixTemplateForDataReader(IpfixIe ... ies  ) {
		int capacity =0;
		this.ies = ies;
		for( IpfixIe ie: this.ies ){
			IpfixFieldSpecifier fld = ie.getFieldSpecifier();
			capacity+=fld.getFieldSpecifierLength();
		}
		this.recordSize = capacity;
		StringBuffer uidSbuf = new StringBuffer();
		this.bbuf = ByteBuffer.allocate(capacity);
		// copying field specifiers to buffer
		for( IpfixIe ie: this.ies ){
			IpfixFieldSpecifier fld = ie.getFieldSpecifier();
			ByteBuffer fldBuf = fld.getBytebuffer();
			fldBuf.limit(fld.getFieldSpecifierLength());
			fldBuf.rewind();
			this.bbuf.put(fldBuf);
			uidSbuf.append(fld.getUid());
		}
		this.uid = uidSbuf.toString();
		
	}
	public IpfixIe [] getInformationElements(){
		return ies;
	}

	public ByteBuffer getByteBuffer(){
		return bbuf;
	}
	/**
	 * Used to match uid in {@link IpfixTemplateRecord}
	 * @return
	 */
	public String getUid() {
		return uid;
	}
	public int getRecordSize() {
		return recordSize;
	}
	
}

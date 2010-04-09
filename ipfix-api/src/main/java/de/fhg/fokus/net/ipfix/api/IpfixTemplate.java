package de.fhg.fokus.net.ipfix.api;

import java.nio.ByteBuffer;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 *
 */
public class IpfixTemplate {
	// -- model --
	private final ByteBuffer bbuf;
	private final IpfixIe [] ies;
	private final String uid;
	/**
	 *
	 * @param ies 
	 */
	public IpfixTemplate(IpfixIe ... ies  ) {
		int capacity =0;
		this.ies = ies;
		for( IpfixIe ie: this.ies ){
			IpfixFieldSpecifier fld = ie.getFieldSpecifier();
			capacity+=fld.getFieldSpecifierLength();
		}
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
}

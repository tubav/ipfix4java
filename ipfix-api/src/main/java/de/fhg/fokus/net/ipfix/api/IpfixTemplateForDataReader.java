package de.fhg.fokus.net.ipfix.api;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

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
	private final Map<IpfixFieldSpecifier, IpfixIe> mapFStoIE;
	private final String uid;
	private final int recordSize; // used for slicing record buffers
	/**
	 *
	 * @param ies 
	 */
	public IpfixTemplateForDataReader(IpfixIe ... ies  ) {
		int capacity =0;
		this.ies = ies;
		this.mapFStoIE = new HashMap<IpfixFieldSpecifier, IpfixIe>(ies.length);
		for( IpfixIe ie: this.ies ){
			mapFStoIE.put(ie.getFieldSpecifier(), ie);
			capacity += ie.getFieldSpecifier().getFieldSpecifierLength();
		}
		this.recordSize = capacity;
		this.bbuf = ByteBuffer.allocate(capacity);
		StringBuffer uidSbuf = new StringBuffer();

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
		// TODO: maybe just use this list
		// return (IpfixIe[]) mapFStoIE.values().toArray();
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
	public boolean hasFieldSpecifier(IpfixFieldSpecifier fs) {
		return mapFStoIE.containsKey(fs);
	}
	
}

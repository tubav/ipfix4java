package de.fhg.fokus.net.ipfix.api;

import java.nio.ByteBuffer;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 *
 */
public interface IpfixDataRecordReader  {
	/**
	 * Reads the next ipfix record and updates setBuffer position accordingly.
	 * 
	 * @param ByteBuffer setBuffer
	 * @param IpfixMessage msg
	 * 
	 * @return ipfix record or null if record could not be read.
	 */
	public Object getRecord( IpfixMessage msg, ByteBuffer setBuffer );
	
	/**
	 * Extract data from the given data record.
	 * 
	 * @param IpfixMessage msg
	 * @param IpfixDataRecord dataRecord
	 * 
	 * @return ipfix record or null if record could not be read.
	 */
	public Object getRecord( IpfixMessage msg, IpfixDataRecord dataRecord );
	
	/**
	 * A Template to define the fields to be read
	 * 
	 * @return IpfixTemplateForDataReader
	 */
	public IpfixTemplateForDataReader getTemplate(); 
	
}

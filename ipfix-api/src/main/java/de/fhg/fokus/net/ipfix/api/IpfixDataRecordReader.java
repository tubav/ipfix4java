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
	 * @param setBuffer
	 * @return ipfix record or null if record could not be read.
	 */
	public IpfixRecord getRecord( ByteBuffer setBuffer );
	public IpfixTemplateForDataReader getTemplate(); 
	
}

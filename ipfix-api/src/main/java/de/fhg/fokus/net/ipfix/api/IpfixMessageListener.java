package de.fhg.fokus.net.ipfix.api;
/**
 * IPFIX Message Listener
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public interface IpfixMessageListener {
	public void onMessage( IpfixMessage msg );
	
}

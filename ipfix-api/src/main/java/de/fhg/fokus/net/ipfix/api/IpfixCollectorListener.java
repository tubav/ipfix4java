package de.fhg.fokus.net.ipfix.api;


/**
 * IPFIX Message Listener
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public interface IpfixCollectorListener {
	public void onConnect( IpfixConnectionHandler handler );
	public void onDisconnect( IpfixConnectionHandler handler );
	public void onMessage( IpfixConnectionHandler handler, IpfixMessage msg );
}

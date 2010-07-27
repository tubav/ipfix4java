package de.fhg.fokus.net.ipfix.api;

import java.net.SocketAddress;

/**
 * IPFIX Handler
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public interface IpfixConnectionHandler {
	public boolean isConnected();
	/**
	 * Get remote address. Note that remote address is available 
	 * even after disconnection.
	 * 
	 * @return remote socket address or null
	 */
	public SocketAddress getRemoteSocketAddress();
	/**
	 * 
	 * @return remote socket address or null
	 */
	public SocketAddress getLocalSocketAddress();
	
}

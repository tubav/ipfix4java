package de.fhg.fokus.net.ipfix.api;

import java.net.Socket;

/**
 * IPFIX Handler
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public interface IpfixConnectionHandler {
	public boolean isConnected();
	public Socket getSocket();
	public Object getAttachment();
	public void setAttachment(Object obj);
	public long totalReceivedMessages();
	
}

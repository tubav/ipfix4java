package de.fhg.fokus.net.ipfix;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.fokus.net.ipfix.api.IpfixDefaultTemplateManager;
import de.fhg.fokus.net.ipfix.api.IpfixHeader;
import de.fhg.fokus.net.ipfix.api.IpfixMessage;

/**
 * IPFIX Collector
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public class IpfixCollector  {
	// -- sys --
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private ExecutorService executor = Executors.newCachedThreadPool();
	// -- model --
	private final IpfixDefaultTemplateManager templateManager = new IpfixDefaultTemplateManager();
	private CopyOnWriteArrayList<ConnectionHandler> clients = new CopyOnWriteArrayList<ConnectionHandler>();
	private CopyOnWriteArrayList<ServerSocket> servers = new CopyOnWriteArrayList<ServerSocket>();
	private class ConnectionHandler {
		// -- constants --
		// -- model --
		private final Socket socket;
		private boolean exit = false;
		public ConnectionHandler(Socket socket) throws IOException {
			logger.debug("Client connected: {}",socket);
			this.socket = socket;
			byte[] hbuf = new byte[IpfixHeader.SIZE_IN_OCTETS];
			InputStream in = socket.getInputStream();
			int offset=0;
			int len = hbuf.length;
			while( !exit ){
				// try to read an ipfix header
				int nbytes= in.read(hbuf, offset, len);
				if(nbytes==len ){
					offset=0;len=0;
					IpfixHeader header= new IpfixHeader(hbuf);
					byte[] mbuf = new byte[header.getLength()];
					nbytes =0;
					len = mbuf.length;
					while(offset==mbuf.length){
						offset+=in.read(mbuf, offset, len);
						len-=offset;
					}
					IpfixMessage msg = new IpfixMessage(templateManager, header, ByteBuffer.wrap(mbuf));
					logger.debug(msg+"");
					
				} else {
					logger.debug("read {} while trying to read header",nbytes);
					offset+=nbytes;
					len-=nbytes;
				}
			}
		}
		public void shutdown(){
			logger.debug("-- closing {}",socket);
			try {
				socket.close();
			} catch (IOException e) {
				logger.error(e+"");
			}
		}
	}
	public void bind(int port) throws IOException {
		final ServerSocket serverSocket = new ServerSocket(port);
		servers.add(serverSocket);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Socket socket = serverSocket.accept();
					clients.add(new ConnectionHandler(socket));
				} catch (IOException e) {
					logger.debug(e+"");
				}
			}
		});
	}

	public void shutdow(){
		logger.info("Closing client sockets");
		for( ConnectionHandler handler : clients ){
			handler.shutdown();
		}
		logger.info("Closing server sockets");
		for( ServerSocket serverSocket: servers ){
			logger.debug("-- closing {}",serverSocket);
			try {
				serverSocket.close();
			} catch (IOException e) {
				logger.error(e+"");
			}
		}
		executor.shutdown();
	}


}

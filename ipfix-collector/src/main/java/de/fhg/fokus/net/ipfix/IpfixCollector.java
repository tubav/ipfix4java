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

import de.fhg.fokus.net.ipfix.api.IpfixDataRecordReader;
import de.fhg.fokus.net.ipfix.api.IpfixDefaultTemplateManager;
import de.fhg.fokus.net.ipfix.api.IpfixHeader;
import de.fhg.fokus.net.ipfix.api.IpfixMessage;
import de.fhg.fokus.net.ipfix.api.IpfixMessageListener;
import de.fhg.fokus.net.ipfix.api.IpfixTemplateManager;

/**
 * <p>IPFIX Collector</p> 
 * <p><b>Usage example</b></p>
 * 
 * <pre>
 * IpfixCollector ic = new IpfixCollector();
 * ic.registerDataRecordReader(IpfixRecordSourceIpv4PacketDeltaCount.getReader());
 * ic.addMessageListener(new IpfixMessageListener() {
 * 	&#064;Override
 * 	public void onMessage(IpfixMessage msg) {
 * 		logger.debug(msg + &quot;&quot;);
 * 		for (IpfixSet set : msg) {
 * 			for (IpfixRecord rec : set) {
 * 				logger.debug(rec + &quot;&quot;);
 * 			}
 * 		}
 * 	}
 * });
 * ic.bind(4739);
 * </pre>
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public final class IpfixCollector {
	// -- sys --
	private final Logger logger = LoggerFactory.getLogger(getClass());
	// TODO used multiple tread pools
	private ExecutorService executor = Executors.newCachedThreadPool();
	// -- ctrl --
	private final IpfixDefaultTemplateManager templateManager = new IpfixDefaultTemplateManager();

	// -- model --
	private CopyOnWriteArrayList<IpfixMessageListener> messageListeners = new CopyOnWriteArrayList<IpfixMessageListener>();
	private CopyOnWriteArrayList<ConnectionHandler> clients = new CopyOnWriteArrayList<ConnectionHandler>();
	private CopyOnWriteArrayList<ServerSocket> servers = new CopyOnWriteArrayList<ServerSocket>();

	private class ConnectionHandler {
		// -- constants --
		// -- model --
		private final Socket socket;
		private boolean exit = false;

		public ConnectionHandler(Socket socket) throws IOException {
			logger.debug("Client connected: {}", socket);
			this.socket = socket;
			InputStream in = socket.getInputStream();
			byte[] bbuf = new byte[1024];
			// ByteBuffer byteBuffer;
			while (!exit) {
				int nbytes = in.read(bbuf);
				if (nbytes > 0) {
					ByteBuffer byteBuffer = ByteBuffer.allocate(nbytes);
					byteBuffer.put(bbuf, 0, nbytes).flip();
					if (IpfixMessage.align(byteBuffer)) {
						IpfixHeader hdr = new IpfixHeader(byteBuffer);
						final IpfixMessage msg = new IpfixMessage(
								IpfixCollector.this.templateManager, hdr,
								byteBuffer);
						// dispatch message to listeners
						for (final IpfixMessageListener lsn : messageListeners) {
							executor.execute(new Runnable() {
								@Override
								public void run() {
									lsn.onMessage(msg);
								}
							});
						}
					}
				}
				if (nbytes == -1) {
					logger.debug("No more data available");
					break;
				}
			}
		}

		public void shutdown() {
			logger.debug("-- closing {}", socket);
			try {
				socket.close();
			} catch (IOException e) {
				logger.error(e + "");
			}
		}
	}

	public void bind(int port) throws IOException {
		final ServerSocket serverSocket = new ServerSocket(port);
		servers.add(serverSocket);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				logger.debug("binding to {}",serverSocket);
				try {
					Socket socket = serverSocket.accept();
					clients.add(new ConnectionHandler(socket));
				} catch (IOException e) {
					logger.debug(e + "");
				}
			}
		});
	}

	public void shutdow() {
		logger.info("Closing client sockets");
		for (ConnectionHandler handler : clients) {
			handler.shutdown();
		}
		logger.info("Closing server sockets");
		for (ServerSocket serverSocket : servers) {
			logger.debug("-- closing {}", serverSocket);
			try {
				serverSocket.close();
			} catch (IOException e) {
				logger.error(e + "");
			}
		}
		executor.shutdown();
	}

	/**
	 * see
	 * {@link IpfixTemplateManager#registerDataRecordReader(IpfixDataRecordReader)}
	 * 
	 */
	public void registerDataRecordReader(IpfixDataRecordReader reader) {
		templateManager.registerDataRecordReader(reader);
	}

	/**
	 * Register a new IPFIX message listener.
	 * 
	 * @param lsn
	 */
	public void addMessageListener(IpfixMessageListener lsn) {
		messageListeners.add(lsn);
	}

	/**
	 * Remove a previous registered IPFIX message listener
	 * 
	 * @param lsn
	 */
	public void removeMessageListener(IpfixMessageListener lsn) {
		messageListeners.remove(lsn);
	}

	/**
	 * Remove all message listeners
	 */
	public void removeAllMessageListeners() {
		messageListeners.clear();
	}

}

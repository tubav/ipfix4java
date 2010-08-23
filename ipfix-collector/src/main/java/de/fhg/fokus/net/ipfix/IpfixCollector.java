package de.fhg.fokus.net.ipfix;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.fokus.net.ipfix.api.IpfixCollectorListener;
import de.fhg.fokus.net.ipfix.api.IpfixConnectionHandler;
import de.fhg.fokus.net.ipfix.api.IpfixDataRecordReader;
import de.fhg.fokus.net.ipfix.api.IpfixDefaultTemplateManager;
import de.fhg.fokus.net.ipfix.api.IpfixHeader;
import de.fhg.fokus.net.ipfix.api.IpfixMessage;
import de.fhg.fokus.net.ipfix.api.IpfixSet;
import de.fhg.fokus.net.ipfix.api.IpfixTemplateManager;
import de.fhg.fokus.net.ipfix.util.ByteBufferUtil;
import de.fhg.fokus.net.ipfix.util.HexDump;

/**
 * <p>
 * IPFIX Collector
 * </p>
 * <p>
 * <b>Usage example</b>
 * </p>
 * 
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
	private CopyOnWriteArrayList<IpfixCollectorListener> eventListeners = new CopyOnWriteArrayList<IpfixCollectorListener>();
	private CopyOnWriteArrayList<ConnectionHandler> clients = new CopyOnWriteArrayList<ConnectionHandler>();
	private CopyOnWriteArrayList<ServerSocket> servers = new CopyOnWriteArrayList<ServerSocket>();
	private enum CollectorEvents {
		CONNECTED,
		DISCONNECTED,
		MESSAGE
	}
	/**
	 * A helper to dispatch events.
	 * 
	 * @param evt
	 * @param handler
	 * @param msg
	 */
	private void dispatchEvent( CollectorEvents evt, final IpfixConnectionHandler handler, final IpfixMessage msg ){
		switch (evt) {
		case CONNECTED:
			for (final IpfixCollectorListener lsn : eventListeners) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						lsn.onConnect(handler);
					}
				});
			}
			break;
		case DISCONNECTED:
			for (final IpfixCollectorListener lsn : eventListeners) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						lsn.onDisconnect(handler);
					}
				});
			}
			break;
		case MESSAGE:
			for (final IpfixCollectorListener lsn : eventListeners) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						lsn.onMessage(handler, msg);
					}
				});
			}
			break;
		default:
			logger.warn("Unsupported event: {}",evt);
			break;
		}
	}
	/**
	 * Connection handler
	 *
	 */
	private class ConnectionHandler implements IpfixConnectionHandler {
		// -- constants --
		// -- model --
		private final Socket socket;
		private boolean exit = false;
		private Object attachment;
		// -- aux --
		private ByteBuffer prevBuffer = null;
		// save remote address for disconnect event
		private SocketAddress remoteAddress =null;

		public ConnectionHandler(Socket socket) throws IOException {
			this.socket = socket;
			if(socket.isConnected()){
				remoteAddress = socket.getRemoteSocketAddress();
				dispatchEvent(CollectorEvents.CONNECTED, this, null);
			}
			InputStream in = socket.getInputStream();
			byte[] bbuf = new byte[1024];
			// ByteBuffer byteBuffer;
			while (!exit) {
				int nbytes = in.read(bbuf);
				if (nbytes > 0) {
					logger.debug("==> nbytes: {}",nbytes);
					ByteBuffer byteBuffer = ByteBuffer.allocate(nbytes);
					byteBuffer.put(bbuf, 0, nbytes).flip();

					// handle previous read
					if( prevBuffer !=null ){
						byteBuffer = ByteBufferUtil.concat( prevBuffer, byteBuffer );
						prevBuffer=null;
					}
					if( !IpfixMessage.enoughData(byteBuffer)){
						prevBuffer=byteBuffer;
						continue;
					}

					// Reading IPFIX messages
					while (IpfixMessage.align(byteBuffer)) {
						IpfixHeader hdr = new IpfixHeader(byteBuffer);
//						if(hdr.getLength()+byteBuffer.position() - IpfixHeader.SIZE_IN_OCTETS > byteBuffer.capacity() ){
//						// message was still not entirely received
//							prevBuffer=byteBuffer;
//							break;
//						}
						logger.debug("msg.len: {} ",hdr.getLength(), byteBuffer.position());
						
						final IpfixMessage msg = new IpfixMessage(
								IpfixCollector.this.templateManager, hdr,
								byteBuffer);
//						logger.debug("msg:  "+HexDump.toHexString(msg.getMessageBuffer()));
//						 dispatch message to listeners
//						dispatchEvent(CollectorEvents.MESSAGE, this, msg);
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

		@Override
		public boolean isConnected() {
			return socket.isConnected();
		}

		@Override
		public Socket getSocket() {
			return socket;
		}

		@Override
		public Object getAttachment() {
			return attachment;
		}

		@Override
		public void setAttachment(Object obj) {
			this.attachment = obj;
		}

	}

	public void bind(int port) throws IOException {
		final ServerSocket serverSocket = new ServerSocket(port);
		servers.add(serverSocket);
		while (true) {
			logger.debug("binding to {}",serverSocket);
			final Socket socket = serverSocket.accept();
			executor.execute(new Runnable() {
				@Override
				public void run() {
					ConnectionHandler handler = null;
					try {
						logger.debug("==> socket: " + socket);
						handler = new ConnectionHandler(socket);
						clients.add(handler);
						logger.debug("handler finished: {}",socket);
						dispatchEvent(CollectorEvents.DISCONNECTED, handler, null);

					} catch (IOException e) {
						logger.debug(e + "");
					}
				}
			});
		}
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
	public void addEventListener(IpfixCollectorListener lsn) {
		eventListeners.add(lsn);
	}

	/**
	 * Remove a previous registered IPFIX message listener
	 * 
	 * @param lsn
	 */
	public void removeEventListener(IpfixCollectorListener lsn) {
		eventListeners.remove(lsn);
	}

	/**
	 * Remove all message listeners
	 */
	public void removeAllEventListeners() {
		eventListeners.clear();
	}

	public static void main(String[] args) throws IOException,
	InterruptedException {

		IpfixCollector ic = new IpfixCollector();

		// register record readers used in application
		// ic.registerDataRecordReader(IpfixRecordImpd4e.getReader());

		// add message listener
		ic.addEventListener(new IpfixCollectorListener() {
			@Override
			public void onMessage(IpfixConnectionHandler handler, IpfixMessage msg) {
				System.out.println("oid: "
						+ msg.getHeader().getObservationDomainID());
				// logger.debug(msg+"");
				for (IpfixSet set : msg) {
					for (Object rec : set) {
						System.out.println(rec + "");
					}
				}
			}

			@Override
			public void onConnect(IpfixConnectionHandler handler) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDisconnect(IpfixConnectionHandler handler) {
				// TODO Auto-generated method stub

			}
		});

		ic.bind(4739);
		System.out.println("sleeping");
		Thread.sleep(10000);

	}
}

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
import de.fhg.fokus.net.ipfix.api.IpfixHeader;
import de.fhg.fokus.net.ipfix.api.IpfixMessage;
import de.fhg.fokus.net.ipfix.api.IpfixSet;
import de.fhg.fokus.net.ipfix.api.IpfixTemplateManager;
import de.fhg.fokus.net.ipfix.api.IpfixTemplateManagerImpl;

/**
 * <p>
 * IPFIX Collector
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
	// TODO: just for debugging
	//private ExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	// -- ctrl --
	//private final IpfixDefaultTemplateManager templateManager = new IpfixDefaultTemplateManager();
	private final IpfixTemplateManager templateManager = new IpfixTemplateManagerImpl();

	// -- model --
	private CopyOnWriteArrayList<IpfixCollectorListener> eventListeners = new CopyOnWriteArrayList<IpfixCollectorListener>();
	private CopyOnWriteArrayList<ConnectionHandler> clients = new CopyOnWriteArrayList<ConnectionHandler>();
	private CopyOnWriteArrayList<ServerSocket> servers = new CopyOnWriteArrayList<ServerSocket>();

	// port to listen for incomming connections
	// default 4379
	private int servicePort = 4379;
	
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
//				executor.execute(new Runnable() {
//					@Override
//					public void run() {
//						lsn.onConnect(handler);
//					}
//				});
				lsn.onConnect(handler);
			}
			break;
		case DISCONNECTED:
			for (final IpfixCollectorListener lsn : eventListeners) {
//				executor.execute(new Runnable() {
//					@Override
//					public void run() {
//						lsn.onDisconnect(handler);
//					}
//				});
				lsn.onDisconnect(handler);
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
		// a connection local template manager
		private final IpfixTemplateManager tm;
		
		private boolean exit = false;
		private volatile Object attachment;
		private long totalReceivedMessages=0;
		// -- aux --
		// save remote address for disconnect event
		@SuppressWarnings("unused")
		private SocketAddress remoteAddress = null;
		public ConnectionHandler(Socket socket){
			this.socket = socket;
			// create a shared copy for this connection
			this.tm = IpfixCollector.this.templateManager.getInstance();
		}
		/**
		 * Starts handler, this will block thread until finished.
		 * 	
		 * @throws IOException
		 */
		public void start() throws IOException {
			if(socket.isConnected()){
				remoteAddress = socket.getRemoteSocketAddress();
				dispatchEvent(CollectorEvents.CONNECTED, this, null);
			}
			InputStream in = socket.getInputStream();
			byte[] bbuf = new byte[1024];
			// create an initial empty previous byte buffer
			ByteBuffer prevBuffer = ByteBuffer.allocate(0);
			
			while (!exit) {
				// this block until something is read
				int nbytes = in.read(bbuf);
				if (nbytes > 0) {
//					logger.debug("==> nbytes: {}",nbytes);
					// create a new buffer with an capacity of the previous buffer and
					// the new read buffer
					ByteBuffer byteBuffer = ByteBuffer.allocate(prevBuffer.remaining()+nbytes);
					// copy previous buffer and read buffer to the working buffer
					// and reset position
					byteBuffer.put(prevBuffer).put(bbuf, 0, nbytes).flip();
					
					// check if buffer contains enough data
					if( !IpfixMessage.enoughData(byteBuffer)){
						prevBuffer=byteBuffer;
						continue;
					}
					
					// Reading IPFIX messages
					while (IpfixMessage.align(byteBuffer)) {
						int pos = byteBuffer.position();
						if(IpfixHeader.getLength(byteBuffer,pos) + pos > byteBuffer.limit()  ){
							// message was still not entirely received
//							logger.debug("message was not entirely received, waiting for more data, buffer.pos:{}",byteBuffer.position());
//							logger.debug("msg len:{}, buffer.limit:{}",IpfixHeader.getLength(byteBuffer,pos),
//									byteBuffer.limit());
							prevBuffer=byteBuffer;
							break;
						}
						
						final IpfixMessage msg = new IpfixMessage(this.tm, byteBuffer);
						totalReceivedMessages++;
//						logger.debug("msg:  "+HexDump.toHexString(msg.getMessageBuffer()));
//						 dispatch message to listeners
						dispatchEvent(CollectorEvents.MESSAGE, this, msg);
					}
					// the remaining byte buffer becomes the new previous buffer
					prevBuffer=byteBuffer;
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

		@Override
		public long totalReceivedMessages() {
			return totalReceivedMessages;
		}

	}

	public void bind(int port) throws IOException {
		final ServerSocket serverSocket = new ServerSocket(port);
		servers.add(serverSocket);
		logger.info("binding to {}",serverSocket);
		while (true) {
			final Socket socket = serverSocket.accept();
			logger.info("accept from {}", socket);
			executor.execute(new Runnable() {
				@Override
				public void run() {
					logger.debug("[ConnectionHandler] start connection reader: {}",socket);
					ConnectionHandler handler = new ConnectionHandler(socket);
					clients.add(handler);
					try {
						handler.start(); 
						logger.debug("[ConnectionHandler] finished normally: {}",socket);
					} catch (IOException e) {
						logger.debug(e + "");
					} finally {
						dispatchEvent(CollectorEvents.DISCONNECTED, handler, null);
					}
				}
			});
		}
	}
	
	public void start() {
		executor.execute(new Runnable() {
			public void run() {
				try {
					bind( servicePort );
				}
				catch( IOException ioe ) {
					logger.debug(ioe.getMessage());
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
						+ msg.getObservationDomainID());
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

	public int getServicePort() {
		return servicePort;
	}

	public void setServicePort(int servicePort) {
		this.servicePort = servicePort;
	}
	
}

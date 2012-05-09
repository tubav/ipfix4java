package de.fhg.fokus.net.ipfix.api;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.fokus.net.ipfix.api.IpfixTemplateManager.Statistics;
import de.fhg.fokus.net.ipfix.util.ByteBufferUtil;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public final class IpfixMessage implements Iterable<IpfixSet> {
	// -- sys --
	private final static Logger logger = LoggerFactory.getLogger(IpfixMessage.class);
	// -- model --
	private ByteBuffer messageBuffer;
	private short numberOfunknownSets = 0;

	// -- management --
	private final IpfixTemplateManager templateManager;
	private final Statistics stats;

	public IpfixMessage(IpfixTemplateManager templateManager, ByteBuffer byteBuffer) {
		this.templateManager = templateManager;
		this.stats = templateManager.getStatistics();
		// slicing
		this.messageBuffer = ByteBufferUtil.sliceAndSkip(byteBuffer, IpfixHeader.getLength(byteBuffer, byteBuffer.position()));
//		byteBuffer.position(byteBuffer.position()+getLength());
			
		stats.globalBufferPosition = byteBuffer.position();

	}
	public void incNumberOfunknownSets(){
		numberOfunknownSets++;
	}
	public short getNumberOfunknownSets() {
		return numberOfunknownSets;
	}

	/**
	 * @return a sliced byte buffer of this message. 
	 * Contents are shared, position and limit are not.
	 */
	public ByteBuffer getMessageBuffer() {
		return messageBuffer.slice();
	}

	/**
	 * @return an iterator over the available sets sent within this ipfix message
	 */
	public Iterator<IpfixSet> iterator() {
		return new Iterator<IpfixSet>() {
			
			private final ByteBuffer setsBuffer = 
					ByteBufferUtil.skipAndSlice(IpfixHeader.SIZE_IN_OCTETS, messageBuffer);
			private IpfixSetHeader currentSetHeader;
			private IpfixSet last = null, next = null;
			
			public boolean hasNext() {
				if (next != null) {
					return true;
				}
				if (setsBuffer.hasRemaining()) {
					try {
						currentSetHeader = new IpfixSetHeader(setsBuffer);
						next = new IpfixSet( IpfixMessage.this
											, templateManager
											, currentSetHeader
											, setsBuffer);
						
						return true;
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(-1);
						logger.error(e.getMessage());
					}
				}
				return false;
			}
			
			public IpfixSet next() {
				if (next == null && !hasNext()) {
					throw new NoSuchElementException();
				}
				last = next;
				next = null;
				return last;
			}
			
			public void remove() {
				throw new UnsupportedOperationException("Cannot remove sets!");
			}
			
		};
	}

	@Override
	public String toString() {
		return String.format("msg:[ %s ]", IpfixHeader.toString(messageBuffer));
	}

	public long getObservationDomainID() {
		return IpfixHeader.getObservationDomainID(messageBuffer);
	}

	public int getVersion() {
		return IpfixHeader.getVersion(messageBuffer);
	}

	public int getLength() {
		return IpfixHeader.getLength(messageBuffer);
	}

	public long getExportTime() {
		return IpfixHeader.getExportTime(messageBuffer);
	}

	public long getSequenceNumber() {
		return IpfixHeader.getSequenceNumber(messageBuffer);
	}

	/**
	 * This currently performs the same function as aligned without logging
	 * messages. The intent is to use in IPFIX collector for buffering data in
	 * case not enough message is available for decoding.
	 * 
	 * @param byteBuffer
	 * @return whether buffer contains a complete IPFIX message
	 */
	public static boolean enoughData(ByteBuffer byteBuffer) {
		if( byteBuffer.remaining() <= IpfixHeader.SIZE_IN_OCTETS ){
			return false;
		}
		// two fingers alignment
		int pos = byteBuffer.position();
		int msg1_version = ByteBufferUtil.getUnsignedShort(byteBuffer, pos
				+ IpfixHeader.IDX_VERSION);
		// first finger
		if (msg1_version == Ipfix.VERSION) {
			int msg1_length = ByteBufferUtil.getUnsignedShort(byteBuffer, pos
					+ IpfixHeader.IDX_LENGTH);
			if (msg1_length == 0) {
				// message length must not be 0;
				return false;
			}
			int limit = byteBuffer.limit();
			if (msg1_length > limit) {
				// message length bigger than available data
				return false;
			}
			if (limit > pos + msg1_length + IpfixHeader.SIZE_IN_OCTETS) {
				// second finger
				return ByteBufferUtil.getUnsignedShort(byteBuffer, pos
						+ msg1_length + IpfixHeader.IDX_VERSION) == Ipfix.VERSION;
			}
			return true;
		}
		return false;

	}

	/**
	 * Align byte buffer containing IPFIX messages. It skips invalid data
	 * positioning the buffer on a valid message. The current implementation
	 * uses two finger alignment.
	 * 
	 */
	public static boolean align(ByteBuffer byteBuffer) {
		// two fingers alignment
		if( byteBuffer.remaining() <= IpfixHeader.SIZE_IN_OCTETS ){
			return false;
		}
		int pos = byteBuffer.position();
		int msg1_version = ByteBufferUtil.getUnsignedShort(byteBuffer, pos
				+ IpfixHeader.IDX_VERSION);
		// first finger
		if (msg1_version == Ipfix.VERSION) {
			int limit = byteBuffer.limit();
			int msg1_length = ByteBufferUtil.getUnsignedShort(byteBuffer, pos
					+ IpfixHeader.IDX_LENGTH);
			if (msg1_length == 0) {
				logger.warn("Strange, message length is 0! I'll skip this "
						+ "and to continue searching for a valid ipfix header.");
				return false;
			}
			if (msg1_length > limit) {
//				logger.debug("Message length ({}) bigger than buffer limit({}), I'll skip this "
//						+ "and to continue searching for a valid ipfix header.",msg1_length,limit);
				return false;
			}
			if (limit > pos + msg1_length
					+ IpfixHeader.SIZE_IN_OCTETS) {
				// second finger
				return ByteBufferUtil.getUnsignedShort(byteBuffer, pos
						+ msg1_length + IpfixHeader.IDX_VERSION) == Ipfix.VERSION;
			}
			return true;
		}

		return false;

	}

}
